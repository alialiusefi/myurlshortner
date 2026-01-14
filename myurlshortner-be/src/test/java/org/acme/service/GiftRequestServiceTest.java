package org.acme.service;

import io.vavr.control.Option;
import org.acme.application.repo.exception.DuplicateAwaitingGiftRequestException;
import org.acme.application.repo.notification.NotificationRepository;
import org.acme.application.service.giftrequest.GiftRequestServiceImpl;
import org.acme.domain.command.CreateGiftRequestCommand;
import org.acme.domain.entity.GiftRequest;
import org.acme.domain.entity.ShortenedUrl;
import org.acme.domain.exceptions.giftrequest.CreateGiftRequestError;
import org.acme.domain.repo.GiftRequestRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class GiftRequestServiceTest {

    @Test
    void shouldSuccessfullyCreateGiftRequest() throws DuplicateAwaitingGiftRequestException {
        var uid = "abcdzxcv12";
        var userSourceId = 1L;
        var userTargetId = 2L;
        var shortenedUrl = new ShortenedUrl(
                URI.create("google.com"),
                uid,
                userSourceId
        );
        var command = new CreateGiftRequestCommand(shortenedUrl, userTargetId);
        var mockRepo = mock(GiftRequestRepository.class);
        var mockNotificationRepo = mock(NotificationRepository.class);
        var service = new GiftRequestServiceImpl(mockRepo, mockNotificationRepo);
        when(mockRepo.getGiftRequestByUniqueIdentifierAndStatusIsAwaiting(uid, null, true)).thenReturn(Optional.empty());
        var result = service.createGiftRequest(command);

        assert result.isEmpty();
        verify(mockRepo).getGiftRequestByUniqueIdentifierAndStatusIsAwaiting(uid, null, true);
        var captor = ArgumentCaptor.forClass(GiftRequest.class);
        verify(mockRepo).saveGiftRequest(captor.capture());
        verify(mockNotificationRepo).saveNotification(any());
        assert captor.getValue().getPublicIdentifier().equals(uid); //todo add better checks
    }

    @Test
    void shouldReturnSameGiftRequestAlreadyExistsForTargetUser() throws DuplicateAwaitingGiftRequestException {
        var uid = "abcdzxcv12";
        var userSourceId = 1L;
        var userTargetId = 2L;
        var shortenedUrl = new ShortenedUrl(
                URI.create("google.com"),
                uid,
                userSourceId
        );
        var command = new CreateGiftRequestCommand(shortenedUrl, userTargetId);
        var mockRepo = mock(GiftRequestRepository.class);
        var mockNotificationRepo = mock(NotificationRepository.class);
        var service = new GiftRequestServiceImpl(mockRepo, mockNotificationRepo);
        var giftRequest = new GiftRequest(
                userSourceId,
                userTargetId,
                uid,
                GiftRequest.GiftRequestStatus.AWAITING,
                OffsetDateTime.now(),
                null
        );
        when(mockRepo.getGiftRequestByUniqueIdentifierAndStatusIsAwaiting(uid, null, true)).thenReturn(Optional.of(giftRequest));

        var result = service.createGiftRequest(command);

        assertThat(result, Matchers.any(Option.class));
        assertThat(result.get(), Matchers.isA(CreateGiftRequestError.TargetUserAlreadyHasSuchGiftRequest.class));
        verify(mockRepo).getGiftRequestByUniqueIdentifierAndStatusIsAwaiting(uid, null, true);
        verify(mockRepo, times(0)).saveGiftRequest(any());
    }

    @Test
    void shouldReturnGiftRequestAlreadyExistsForShortenedUrl() throws DuplicateAwaitingGiftRequestException {
        var uid = "abcdzxcv12";
        var userSourceId = 1L;
        var userTargetId = 2L;
        var shortenedUrl = new ShortenedUrl(
                URI.create("google.com"),
                uid,
                userSourceId
        );
        var command = new CreateGiftRequestCommand(shortenedUrl, userTargetId);
        var mockRepo = mock(GiftRequestRepository.class);
        var mockNotificationRepo = mock(NotificationRepository.class);
        var service = new GiftRequestServiceImpl(mockRepo, mockNotificationRepo);
        var giftRequest = new GiftRequest(
                userSourceId,
                3L,
                uid,
                GiftRequest.GiftRequestStatus.AWAITING,
                OffsetDateTime.now(),
                null
        );
        when(mockRepo.getGiftRequestByUniqueIdentifierAndStatusIsAwaiting(uid, null, true)).thenReturn(Optional.of(giftRequest));

        var result = service.createGiftRequest(command);

        assertThat(result, Matchers.any(Option.class));
        assertThat(result.get(), Matchers.isA(CreateGiftRequestError.ShortenedUrlAlreadyHasAGiftRequest.class));
        verify(mockRepo).getGiftRequestByUniqueIdentifierAndStatusIsAwaiting(uid, null, true);
        verify(mockRepo, times(0)).saveGiftRequest(any());
    }

    @Test
    void shouldReturnGiftRequestCannotBeSentToTheSourceUser() throws DuplicateAwaitingGiftRequestException {
        var uid = "abcdzxcv12";
        var userSourceId = 1L;
        var userTargetId = 1L;
        var shortenedUrl = new ShortenedUrl(
                URI.create("google.com"),
                uid,
                userSourceId
        );
        var command = new CreateGiftRequestCommand(shortenedUrl, userTargetId);
        var mockRepo = mock(GiftRequestRepository.class);
        var mockNotificationRepo = mock(NotificationRepository.class);
        var service = new GiftRequestServiceImpl(mockRepo, mockNotificationRepo);

        var result = service.createGiftRequest(command);

        assertThat(result, Matchers.any(Option.class));
        assertThat(result.get(), Matchers.isA(CreateGiftRequestError.GiftRequestTargetUserCannotBeTheSourceUser.class));
        verify(mockRepo, times(0)).getGiftRequestByUniqueIdentifierAndStatusIsAwaiting(uid, null, true);
        verify(mockRepo, times(0)).saveGiftRequest(any());
    }
}
