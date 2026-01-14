package org.acme.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.path.json.mapper.factory.Jackson2ObjectMapperFactory;
import jakarta.inject.Inject;
import org.acme.application.controller.Constants;
import org.acme.application.controller.giftrequest.GetAwaitingGiftRequestResponse;
import org.acme.application.repo.eventstore.ShortenedUrlEventRepository;
import org.acme.application.repo.exception.DuplicateAwaitingGiftRequestException;
import org.acme.application.repo.notification.NotificationRepository;
import org.acme.domain.entity.GiftRequest;
import org.acme.domain.entity.ShortenedUrl;
import org.acme.domain.events.ShortenedUrlEventEnvelopFactory;
import org.acme.domain.repo.GiftRequestRepository;
import org.acme.domain.repo.SaveShortenedUrlConflictError;
import org.acme.domain.repo.ShortenedUrlRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.net.URI;
import java.time.OffsetDateTime;

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

    @Inject
    NotificationRepository notificationRepository;

    @Inject
    ObjectMapper mapper;
    Jackson2ObjectMapperFactory factory = new Jackson2ObjectMapperFactory() {
        @Override
        public ObjectMapper create(Type cls, String charset) {
            return mapper;
        }
    };
    JsonPathConfig config = JsonPathConfig.jsonPathConfig().with().jackson2ObjectMapperFactory(factory);

    @BeforeEach
    void cleanup() {
        this.repository.cleanup();
        this.eventStore.cleanup();
    }

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
                                    "target_user_id": 2
                                }
                                """.stripIndent()
                )
                .post("/shortened-urls/" + uid + "/gift-requests")
                .then()
                .statusCode(201);
        assertThat(repository.getGiftRequestByUniqueIdentifierAndStatusIsAwaiting(uid, null, false).isPresent(), Matchers.equalTo(true));
        assertThat(notificationRepository.getLatestNotificationsByUserId(2L, 1), Matchers.hasSize(1));
    }

    @Test
    void shouldGetAwaitingGiftRequest() throws DuplicateAwaitingGiftRequestException {
        var pubId = "abcdabcd12";
        var giftRequest = new GiftRequest(
                1L,
                2L,
                pubId,
                GiftRequest.GiftRequestStatus.AWAITING,
                OffsetDateTime.now(),
                null
        );
        var id = repository.saveGiftRequest(giftRequest);

        var result = given()
                .header(new Header(Constants.USER_ID_HEADER_KEY, "1"))
                .get("/shortened-urls/" + pubId + "/gift-requests/awaiting")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath(config).getObject("", GetAwaitingGiftRequestResponse.class);

        assertThat(result.id(), Matchers.equalTo(id));
        assertThat(result.updatedAt(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    void shoultCancelAwaitingGiftRequest() throws DuplicateAwaitingGiftRequestException {
        var pubId = "abcdabcd12";
        var giftRequest = new GiftRequest(
                1L,
                2L,
                pubId,
                GiftRequest.GiftRequestStatus.AWAITING,
                OffsetDateTime.now(),
                null
        );
        var id = repository.saveGiftRequest(giftRequest);

        given()
                .header(new Header(Constants.USER_ID_HEADER_KEY, "1"))
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "updated_at" : null
                        }
                        """.stripIndent())
                .put("/gift-requests/awaiting/" + id + "/cancel")
                .then()
                .statusCode(204);
    }
}
