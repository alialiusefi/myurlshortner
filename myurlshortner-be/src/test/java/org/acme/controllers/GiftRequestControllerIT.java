package org.acme.controllers;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import jakarta.inject.Inject;
import org.acme.application.controller.Constants;
import org.acme.application.repo.eventstore.ShortenedUrlEventRepository;
import org.acme.domain.entity.ShortenedUrl;
import org.acme.domain.events.ShortenedUrlEventEnvelopFactory;
import org.acme.domain.repo.GiftRequestRepository;
import org.acme.domain.repo.SaveShortenedUrlConflictError;
import org.acme.domain.repo.ShortenedUrlRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;

@QuarkusTest
public class GiftRequestControllerIT {

    @Inject
    ShortenedUrlRepository shortenedUrlRepository;

    @Inject
    ShortenedUrlEventRepository eventStore;

    @Inject
    GiftRequestRepository repository;

    @Test
    void shouldCreateGiftRequest() throws SaveShortenedUrlConflictError {
        var url = "youtube.com";
        var uid = "abcdefghi2";
        var userId = 1L;
        var entity = new ShortenedUrl(URI.create(url), uid, userId);
        eventStore.insertEvent(ShortenedUrlEventEnvelopFactory.createV1CreatedShortenUrlEvent(entity));
        shortenedUrlRepository.insertShortenedUrl(entity);

        given()
                .header(new Header(Constants.USER_ID_HEADER_KEY, "1"))
                .contentType(ContentType.JSON)
                .body(
                        """
                                {
                                    "target_user_id": 2,
                                }
                                """.stripIndent()
                )
                .post("/shortened-urls/" + uid + "/gift-requests")
                .then()
                .statusCode(201);
        assertThat(repository.getGiftRequestByUniqueIdentifierAndStatusIsAwaiting(uid, null).isPresent(), Matchers.equalTo(true));
    }
}
