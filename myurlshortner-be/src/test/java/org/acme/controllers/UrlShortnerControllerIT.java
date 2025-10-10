package org.acme.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.path.json.mapper.factory.Jackson2ObjectMapperFactory;
import jakarta.inject.Inject;
import org.acme.application.controller.url.UrlList;
import org.acme.application.kafka.KafkaUrlPublisherLocal;
import org.acme.application.repo.eventstore.ShortenedUrlEventRepository;
import org.acme.application.repo.urlshortner.ShortenedUrlRepositoryImpl;
import org.acme.domain.entity.ShortenedUrl;
import org.acme.domain.events.ShortenedUrlRecordType;
import org.acme.domain.repo.SaveShortenedUrlError;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Type;
import java.net.URI;
import java.time.OffsetDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;

@QuarkusTest
class UrlShortnerControllerIT {

    @Inject
    ObjectMapper mapper;

    @Inject
    ShortenedUrlRepositoryImpl repo;

    @Inject
    ShortenedUrlEventRepository eventStore;

    @InjectMock
    KafkaUrlPublisherLocal publisher;

    Jackson2ObjectMapperFactory factory = new Jackson2ObjectMapperFactory() {
        @Override
        public ObjectMapper create(Type cls, String charset) {
            return mapper;
        }
    };

    JsonPathConfig config = JsonPathConfig.jsonPathConfig().with().jackson2ObjectMapperFactory(factory);

    @BeforeEach
    void cleanup() throws SaveShortenedUrlError {
        repo.cleanup();
    }

    @Test
    void testGetUrlsEndpoint() throws SaveShortenedUrlError {
        var datetime = OffsetDateTime.now();
        repo.insertShortenedUrl(new ShortenedUrl("https://www.google.com", "abcdefghik", datetime, datetime, true));
        repo.insertShortenedUrl(new ShortenedUrl("https://www.dis.com", "abcdefghi2", datetime, datetime, false));
        var result = given()
                .when().get("/shortened-urls?page=1&size=10")
                .then()
                .statusCode(200)
                .body("total", Matchers.equalTo(2));
        var data = result.extract().jsonPath(config).getList("data", UrlList.UrlRow.class);
        assertThat(data, Matchers.not(Matchers.empty()));
        assertThat(data, Matchers.contains(
                new UrlList.UrlRow("https://www.google.com", "http://localhost/goto/abcdefghik", 0L, datetime, true),
                new UrlList.UrlRow("https://www.dis.com", "http://localhost/goto/abcdefghi2", 0L, datetime, false)
        ));
    }

    @Test
    void testGenerateShortenedUrl() {
        var body = """
                    {
                        "url": "https://www.example.com"
                    }
                """.stripIndent();
        var shortenedUrl = given()
                .body(body)
                .contentType(ContentType.JSON)
                .when()
                .post("/shorten")
                .then()
                .statusCode(201)
                .body("shortened_url", Matchers.startsWith("http://localhost/goto/"))
                .extract().body().jsonPath().getString("shortened_url");
        var uid = shortenedUrl.substring(shortenedUrl.lastIndexOf("/") + 1);
        var maybeShortenedUrl = repo.getShortenedUrl(uid);
        var event = eventStore.getLatestShortenedUrlEventByIdAndType(uid, ShortenedUrlRecordType.USER_CREATED_SHORTENED_URL);

        assertThat("Shortened url exists", maybeShortenedUrl.isPresent());
        assertThat("Event exists", event.isPresent());
        assertThat("Starts with https", maybeShortenedUrl.get().getOriginalUrl().toString().startsWith("https"));
        assertThat("Enabled", maybeShortenedUrl.get().isEnabled());
        Mockito.verify(publisher).publishUserCreatedShortenedUrl(
                Mockito.any(OffsetDateTime.class),
                Mockito.any(URI.class),
                Mockito.any(String.class)
        );
    }

    @Test
    void testUpdateShortenedUrl() throws SaveShortenedUrlError {
        var url = "youtube.com";
        var uid = "abcdefghik";
        var entity = new ShortenedUrl(URI.create(url), uid);
        repo.insertShortenedUrl(entity);

        var body = """
                {
                    "url": "google.com",
                    "is_enabled": true
                }
                """.stripIndent();
        given()
                .body(body)
                .contentType(ContentType.JSON)
                .when()
                .patch(String.format("/shortened-urls/%s", uid))
                .then()
                .statusCode(204);

        var found = repo.getShortenedUrl(uid);
        var event = eventStore.getLatestShortenedUrlEventByIdAndType(uid, ShortenedUrlRecordType.USER_UPDATED_ORIGINAL_URL);
        assertThat("Shortened url exists", found.isPresent());
        var foundShortenedUrl = found.get();
        assertThat("Url has changed", !foundShortenedUrl.getOriginalUrl().equals(URI.create("google.com")));
        assertThat("Event exists", event.isPresent());
        assertThat("Updated at has changed", foundShortenedUrl.getUpdatedAt().isAfter(entity.getUpdatedAt()));
        assertThat("Created at didn't change", foundShortenedUrl.getCreatedAt().isEqual(entity.getCreatedAt()));
        assertThat("Enabled", foundShortenedUrl.isEnabled());
        //Mockito.verify(publisher).publishUserUpdatedOriginalUrl(Mockito.any(V1UserUpdatedOriginalUrlEvent.class));
    }

    @Test
    void testDisableShortenedUrl() throws SaveShortenedUrlError {
        var url = "https://www.google.com";
        var uid = "abcdefghik";
        var entity = new ShortenedUrl(URI.create(url), uid);
        repo.insertShortenedUrl(entity);

        var body = """
                {
                    "url": "google.com",
                    "is_enabled": false
                }
                """.stripIndent();
        given()
                .body(body)
                .contentType(ContentType.JSON)
                .when()
                .patch(String.format("/shortened-urls/%s", uid))
                .then()
                .statusCode(204);

        var found = repo.getShortenedUrl(uid);
        var event = eventStore.getLatestShortenedUrlEventByIdAndType(uid, ShortenedUrlRecordType.USER_UPDATED_ORIGINAL_URL);
        assertThat("Shortened url exists", found.isPresent());
        var foundShortenedUrl = found.get();
        assertThat("Url has not changed", foundShortenedUrl.getOriginalUrl().equals(URI.create("https://www.google.com")));
        assertThat("Event doesnt exists", event.isEmpty());
        assertThat("Updated at has changed", foundShortenedUrl.getUpdatedAt().isAfter(entity.getUpdatedAt()));
        assertThat("Created at didn't change", foundShortenedUrl.getCreatedAt().isEqual(entity.getCreatedAt()));
        assertThat("Disabled", !foundShortenedUrl.isEnabled());
        //Mockito.verify(publisher, times(0)).publishUserUpdatedOriginalUrl(Mockito.any(V1UserUpdatedOriginalUrlEvent.class));
    }

    @Test
    void testUpdateShortenedUrlNotFound() throws SaveShortenedUrlError {
        var body = """
                {
                    "url": "google.com"
                }
                """.stripIndent();
        given()
                .body(body)
                .contentType(ContentType.JSON)
                .when()
                .patch("/shortened-urls/abcdabcd12")
                .then()
                .statusCode(404);
    }

    @Test
    void testUpdateShortenedUrlBadRequest() throws SaveShortenedUrlError {
        var url = "youtube.com";
        var uid = "abcdefghik";
        var entity = new ShortenedUrl(URI.create(url), uid);
        repo.insertShortenedUrl(entity);

        var body = """
                {
                    "url": "something"
                }
                """.stripIndent();
        given()
                .body(body)
                .contentType(ContentType.JSON)
                .when()
                .patch("/shortened-urls/abcdefghik")
                .then()
                .statusCode(400);
        var body2 = """
                {
                    "url": ""
                }
                """.stripIndent();
        given()
                .body(body2)
                .contentType(ContentType.JSON)
                .when()
                .patch("/shortened-urls/abcdefghik")
                .then()
                .statusCode(400);
    }
}
