package org.acme.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.path.json.mapper.factory.Jackson2ObjectMapperFactory;
import jakarta.inject.Inject;
import org.acme.application.controller.urlshortner.UrlShortnerList;
import org.acme.application.repo.urlshortner.ShortenedUrlRepositoryImpl;
import org.acme.domain.repo.ShortenedUrlRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;

@QuarkusTest
class UrlShortnerControllerIT {

    @Inject
    ObjectMapper mapper;

    @Inject
    ShortenedUrlRepositoryImpl repo;

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
    }

    @Test
    void testGetUrlsEndpoint() {
        var result = given()
                .when().get("/shortened-urls?page=1&size=10")
                .then()
                .statusCode(200)
                .body("total", Matchers.equalTo(1));
        var data = result.extract().jsonPath(config).getList("data", UrlShortnerList.UrlShortnerListRow.class);
        assertThat(data, Matchers.not(Matchers.empty()));
        assertThat(data, Matchers.contains(
                new UrlShortnerList.UrlShortnerListRow("https://www.google.com", "http://localhost/goto/abcdefghik")
        ));
    }

    @Test
    void testGenerateShortenedUrl() {
        var body = """
                    {
                        "url": "https://www.example.com"
                    }
                """.stripIndent();
        given()
                .body(body)
                .contentType(ContentType.JSON)
                .when()
                .post("/shorten")
                .then()
                .statusCode(201)
                .body("shortened_url", Matchers.startsWith("http://localhost/goto/"));
    }
}
