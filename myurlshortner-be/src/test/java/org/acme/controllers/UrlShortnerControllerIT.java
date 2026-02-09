package org.acme.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.path.json.mapper.factory.Jackson2ObjectMapperFactory;
import jakarta.inject.Inject;
import org.acme.application.controller.Constants;
import org.acme.application.controller.url.ShortenedUrlHistoryResponse;
import org.acme.application.controller.url.ShortenedUrlResponse;
import org.acme.application.controller.url.UrlList;
import org.acme.application.kafka.KafkaUrlPublisherLocal;
import org.acme.application.repo.eventstore.ShortenedUrlEventRepository;
import org.acme.application.repo.urlshortner.ShortenedUrlRepositoryImpl;
import org.acme.domain.entity.ShortenedUrl;
import org.acme.domain.events.ShortenedUrlEventEnvelopFactory;
import org.acme.domain.events.ShortenedUrlRecordType;
import org.acme.domain.repo.SaveShortenedUrlConflictError;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Type;
import java.net.URI;
import java.time.OffsetDateTime;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
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
    void cleanup() {
        repo.cleanup();
        eventStore.cleanup();
    }

    @Test
    void testGetUrlsEndpoint() throws SaveShortenedUrlConflictError {
        var datetime = OffsetDateTime.now();
        var datetime2 = OffsetDateTime.now();
        repo.insertShortenedUrl(new ShortenedUrl("https://www.google.com", "abcdefghik", datetime, datetime, true, 1L, "test1"));
        repo.insertShortenedUrl(new ShortenedUrl("https://www.dis.com", "abcdefghi2", datetime2, datetime2, false, 1L, "test2"));
        var result = given()
                .when().get("/shortened-urls?page=1&size=10")
                .then()
                .statusCode(200)
                .body("total", Matchers.equalTo(2));
        var data = result.extract().jsonPath(config).getList("data", UrlList.UrlRow.class);
        assertThat(data, Matchers.not(Matchers.empty()));
        assertThat(data, Matchers.contains(
                new UrlList.UrlRow("abcdefghi2", "https://www.dis.com", "http://localhost/goto/abcdefghi2", 0L, datetime2, false, "test2"),
                new UrlList.UrlRow("abcdefghik", "https://www.google.com", "http://localhost/goto/abcdefghik", 0L, datetime, true, "test1")
        ));
    }

    @Test
    void generateUniqueId() {
        given()
                .post("/unique-identifiers")
                .then()
                .statusCode(201);
    }

    @Test
    void testCreateShortenedUrl() {
        var body = """
                    {
                        "url": "https://www.example.com"
                    }
                """.stripIndent();
        var shortenedUrl = given()
                .body(body)
                .contentType(ContentType.JSON)
                .header(new Header(Constants.USER_ID_HEADER_KEY, "1"))
                .when()
                .post("/shorten")
                .then()
                .statusCode(201)
                .body("shortened_url", Matchers.startsWith("http://localhost/goto/"))
                .extract().body().jsonPath().getString("shortened_url");
        var uid = shortenedUrl.substring(shortenedUrl.lastIndexOf("/") + 1);
        var maybeShortenedUrl = repo.getShortenedUrl(uid, 1L);
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
    void testCreateShortenedUrlWithIdentifier() {
        var body = """
                    {
                        "url": "https://www.example.com",
                        "unique_identifier": "abcd"
                    }
                """.stripIndent();
        var shortenedUrl = given()
                .body(body)
                .contentType(ContentType.JSON)
                .header(new Header(Constants.USER_ID_HEADER_KEY, "1"))
                .when()
                .post("/shorten")
                .then()
                .statusCode(201)
                .body("shortened_url", Matchers.startsWith("http://localhost/goto/"))
                .extract().body().jsonPath().getString("shortened_url");
        var uid = shortenedUrl.substring(shortenedUrl.lastIndexOf("/") + 1);
        var maybeShortenedUrl = repo.getShortenedUrl(uid, 1L);
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
    void testUpdateShortenedUrl() throws SaveShortenedUrlConflictError {
        var userId = 1L;
        var url = "youtube.com";
        var uid = "abcdefghik";
        var entity = new ShortenedUrl(URI.create(url), uid, userId, "title");
        repo.insertShortenedUrl(entity);
        eventStore.insertEvent(
                ShortenedUrlEventEnvelopFactory.createV2CreatedShortenUrlEvent(
                        entity
                )
        );
        var body = """
                {
                    "url": "google.com",
                    "is_enabled": true
                }
                """.stripIndent();
        given()
                .body(body)
                .contentType(ContentType.JSON)
                .header(new Header(Constants.USER_ID_HEADER_KEY, "1"))
                .when()
                .patch(String.format("/shortened-urls/%s", uid))
                .then()
                .statusCode(200);

        var found = repo.getShortenedUrl(uid, userId);
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
    void testDisableShortenedUrl() throws SaveShortenedUrlConflictError {
        var userId = 1L;
        var url = "https://www.google.com";
        var uid = "abcdefghic";
        var entity = new ShortenedUrl(URI.create(url), uid, userId, "");
        repo.insertShortenedUrl(entity);
        eventStore.insertEvent(
                ShortenedUrlEventEnvelopFactory.createV2CreatedShortenUrlEvent(
                        entity
                )
        );
        var body = """
                {
                    "url": "google.com",
                    "is_enabled": false
                }
                """.stripIndent();
        given()
                .body(body)
                .contentType(ContentType.JSON)
                .header(new Header(Constants.USER_ID_HEADER_KEY, "1"))
                .when()
                .patch(String.format("/shortened-urls/%s", uid))
                .then()
                .statusCode(200);

        var found = repo.getShortenedUrl(uid, userId);
        var event = eventStore.getLatestShortenedUrlEventByIdAndType(uid, ShortenedUrlRecordType.USER_UPDATED_ORIGINAL_URL);
        assertThat("Shortened url exists", found.isPresent());
        var foundShortenedUrl = found.get();
        assertThat("Url has not changed", foundShortenedUrl.getOriginalUrl().equals(URI.create("https://www.google.com")));
        assertThat("Url has not changed", foundShortenedUrl.getTitle().isEmpty());
        assertThat("Event doesnt exists", event.isEmpty());
        assertThat("Updated at has changed", foundShortenedUrl.getUpdatedAt().isAfter(entity.getUpdatedAt()));
        assertThat("Created at didn't change", foundShortenedUrl.getCreatedAt().isEqual(entity.getCreatedAt()));
        assertThat("Disabled", !foundShortenedUrl.isEnabled());
        //Mockito.verify(publisher, times(0)).publishUserUpdatedOriginalUrl(Mockito.any(V1UserUpdatedOriginalUrlEvent.class));
    }

    @Test
    void testUpdateShortenedUrlNotFound() throws SaveShortenedUrlConflictError {
        var body = """
                {
                    "url": "google.com"
                }
                """.stripIndent();
        given()
                .body(body)
                .contentType(ContentType.JSON)
                .header(new Header(Constants.USER_ID_HEADER_KEY, "1"))
                .when()
                .patch("/shortened-urls/abcdabcd12")
                .then()
                .statusCode(404);
    }

    @Test
    void testUpdateShortenedUrlBadRequest() throws SaveShortenedUrlConflictError {
        var userId = 1L;
        var url = "youtube.com";
        var uid = "abcdefghik";
        var entity = new ShortenedUrl(URI.create(url), uid, userId, null);
        repo.insertShortenedUrl(entity);
        eventStore.insertEvent(
                ShortenedUrlEventEnvelopFactory.createV2CreatedShortenUrlEvent(
                        entity
                )
        );
        var body = """
                {
                    "url": "something"
                }
                """.stripIndent();
        given()
                .body(body)
                .contentType(ContentType.JSON)
                .header(new Header(Constants.USER_ID_HEADER_KEY, "1"))
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

    @Test
    void testGetShortenedUrlHistory() throws SaveShortenedUrlConflictError {
        var url = "youtube.com";
        var uid = "abcdefghi2";
        var userId = 1L;
        var entity = new ShortenedUrl(URI.create(url), uid, userId, null);
        repo.insertShortenedUrl(entity);

        eventStore.insertEvent(
                ShortenedUrlEventEnvelopFactory.createV2CreatedShortenUrlEvent(
                        entity
                )
        );
        eventStore.insertEvent(
                ShortenedUrlEventEnvelopFactory.createV1UpdatedOriginalUrlEvent(
                        entity,
                        URI.create("abc.com")
                )
        );
        eventStore.insertEvent(
                ShortenedUrlEventEnvelopFactory.createV1UpdatedOriginalUrlEvent(
                        entity,
                        URI.create("yahoo.com")
                )
        );

        String now = OffsetDateTime.now().plusMinutes(1L).toString();
        var response = given().header(new Header(Constants.USER_ID_HEADER_KEY, "1"))
                .get("/shortened-urls/" + uid + "/history?offset=1&size=1&from=" + now)
                .then()
                .statusCode(200)
                .extract().jsonPath(config).getList("data", ShortenedUrlHistoryResponse.UrlUpdatedHistoryRow.class);

        assertThat("Size is correct", response.size() == 1);
        assertThat("Content is correct", response.getFirst().getUrl().equals("abc.com"));
    }

    @Test
    void testGetShortenedUrlsHistory404() {
        String uid = "unknown123";
        when().get("/shortened-urls/" + uid + "/history")
                .then()
                .statusCode(404);
    }

    @Test
    void testGetShortenedUrl404() {
        String uid = "unknown123";
        when().get("/shortened-urls/" + uid)
                .then()
                .statusCode(404);
    }

    @Test
    void testGetShortenedUrl() throws SaveShortenedUrlConflictError {
        var userId = 1L;
        var url = "youtube.com";
        var uid = "abcdefgh45";
        var entity = new ShortenedUrl(URI.create(url), uid, userId, "some");
        repo.insertShortenedUrl(entity);

        eventStore.insertEvent(
                ShortenedUrlEventEnvelopFactory.createV2CreatedShortenUrlEvent(
                        entity
                )
        );
        eventStore.insertEvent(
                ShortenedUrlEventEnvelopFactory.createV1UpdatedOriginalUrlEvent(
                        entity,
                        URI.create("abc.com")
                )
        );
        eventStore.insertEvent(
                ShortenedUrlEventEnvelopFactory.createV1UpdatedOriginalUrlEvent(
                        entity,
                        URI.create("yahoo.com")
                )
        );
        entity.setIsEnabled(false);
        repo.updateShortenedUrl(entity, entity.getUpdatedAt());

        var response = given()
                .header(new Header(Constants.USER_ID_HEADER_KEY, "1"))
                .get("/shortened-urls/" + uid)
                .then()
                .statusCode(200)
                .extract().jsonPath(config).getObject("", ShortenedUrlResponse.class);

        assertThat("Url is correct", response.url().equals("yahoo.com"));
        assertThat("Url is correct", response.title().equals("some"));
        assertThat("Is Enabled is correct", !response.isEnabled());
        assertThat("Updated at is correct", !response.updatedAt().equals(entity.getUpdatedAt()));
        assertThat("Created at is correct", response.createdAt().equals(entity.getCreatedAt()));
    }
}
