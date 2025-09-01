package org.acme.controllers;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.acme.domain.ShortenedUrl;
import org.acme.domain.repo.SaveShortenedUrlError;
import org.acme.domain.repo.ShortenedUrlRepository;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class UrlControllerIT {

    @Inject
    public ShortenedUrlRepository repo;

    @Test
    void shouldReturnTemporaryRedirect() throws SaveShortenedUrlError {
        repo.insertShortenedUrl(new ShortenedUrl(URI.create("http://www.example.com"), "abcdeabcde"));
        given()
                .header("User-Agent", "Test/V1")
                .when()
                .redirects().follow(false)
                .get("/urls/abcdeabcde")
                .then().statusCode(307);
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
