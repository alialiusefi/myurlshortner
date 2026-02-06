package org.acme.controllers;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.acme.application.repo.eventstore.ShortenedUrlEventRepository;
import org.acme.application.repo.urlshortner.ShortenedUrlRepositoryImpl;
import org.acme.domain.entity.ShortenedUrl;
import org.acme.domain.events.ShortenedUrlEventEnvelopFactory;
import org.acme.domain.repo.SaveShortenedUrlConflictError;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class UrlControllerIT {

    @Inject
    public ShortenedUrlRepositoryImpl repo;

    @Inject
    public ShortenedUrlEventRepository eventStore;

    @BeforeEach
    void cleanup() {
        repo.cleanup();
        eventStore.cleanup();
    }

    @Test
    void shouldReturnTemporaryRedirect() throws SaveShortenedUrlConflictError {
        var originalUrl = URI.create("http://www.example.com");
        var entity = new ShortenedUrl(originalUrl, "abcdeabcde", 1L, "neo");
        repo.insertShortenedUrl(entity);
        eventStore.insertEvent(
                ShortenedUrlEventEnvelopFactory.createV2CreatedShortenUrlEvent(
                        entity
                )
        );
        given()
                .header("User-Agent", "Test/V1")
                .when()
                .redirects().follow(false)
                .get("/urls/abcdeabcde")
                .then()
                .statusCode(307)
                .header("Location", Matchers.is(originalUrl.toString()));
    }

    @Test
    void shouldReturn404WhenDoesntExist() {
        given()
                .when().get("/urls/abcde")
                .then().statusCode(404);
    }

    @Test
    void shouldValidateUniqueIdentifier() {
        given()
                .header("User-Agent", "Test/V1")
                .when().get("/urls/abcdeabcde12")
                .then().statusCode(400);

    }

    @Test
    void shouldValidateUserAgent() {
        given()
                .header("User-Agent", "")
                .when().get("/urls/abcde")
                .then().statusCode(400);

    }
}
