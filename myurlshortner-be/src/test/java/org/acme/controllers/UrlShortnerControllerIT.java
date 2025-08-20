package org.acme.controllers;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
class UrlShortnerControllerIT {
    @Test
    void testGetUrlsEndpoint() {
        given()
                .when().get("/shortened-urls")
                .then()
                .statusCode(200);
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
                .statusCode(201);
    }
}
