package org.acme.service;

import org.acme.application.repo.notification.NotificationRepository;
import org.acme.application.service.notification.NotificationServiceImpl;
import org.acme.domain.entity.Notification;
import org.acme.domain.entity.NotificationParams;
import org.acme.domain.entity.NotificationType;
import org.acme.domain.exceptions.NotificationIsAlreadyRead;
import org.acme.domain.exceptions.NotificationIsNotFound;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class NotificationServiceTest {

    @Test
    public void shouldReadNotification() {
        var userId = 1L;
        var uid = "abcdabcd11";
        var notification = new Notification(
                1L,
                uid,
                NotificationType.SHORTENED_URL_REACHED_N_VIEWS,
                new NotificationParams.ShortenedUrlReachedNViewsParams(uid, 10L),
                userId,
                OffsetDateTime.now(),
                null
        );
        var repo = mock(NotificationRepository.class);
        when(repo.getNotificationById(notification.id(), userId)).thenReturn(Optional.of(notification));
        when(repo.setNotificationReadAtByIdAndUserId(any(OffsetDateTime.class), eq(notification.id()), eq(userId))).thenReturn(1);
        var service = new NotificationServiceImpl(repo);

        service.readNotification(userId, notification.id());

        verify(repo).getNotificationById(notification.id(), userId);
        verify(repo).setNotificationReadAtByIdAndUserId(any(OffsetDateTime.class), eq(notification.id()), eq(userId));
    }

    @Test
    public void shouldReturnNotFoundErrorWhenReadingMismatchUserNotification() {
        var userId = 1L;
        var uid = "abcdabcd11";
        var notification = new Notification(
                1L,
                uid,
                NotificationType.SHORTENED_URL_REACHED_N_VIEWS,
                new NotificationParams.ShortenedUrlReachedNViewsParams(uid, 10L),
                2L,
                OffsetDateTime.now(),
                null
        );
        var repo = mock(NotificationRepository.class);
        when(repo.getNotificationById(notification.id(), userId)).thenReturn(Optional.empty());
        var service = new NotificationServiceImpl(repo);

        var result = service.readNotification(userId, notification.id());

        assert result.get() instanceof NotificationIsNotFound;
    }

    @Test
    public void shouldReturnNotFoundErrorWhenReadingNonExistentNotification() {
        var userId = 1L;
        var uid = "abcdabcd11";
        var notification = new Notification(
                2L,
                uid,
                NotificationType.SHORTENED_URL_REACHED_N_VIEWS,
                new NotificationParams.ShortenedUrlReachedNViewsParams(uid, 10L),
                userId,
                OffsetDateTime.now(),
                null
        );
        var repo = mock(NotificationRepository.class);
        when(repo.getNotificationById(notification.id(), userId)).thenReturn(Optional.empty());
        var service = new NotificationServiceImpl(repo);

        var result = service.readNotification(userId, notification.id());

        assert result.get() instanceof NotificationIsNotFound;
    }

    @Test
    public void shouldReturnNotificationAlreadyReadWhenReadingReadNotification() {
        var userId = 1L;
        var uid = "abcdabcd11";
        var notification = new Notification(
                1L,
                uid,
                NotificationType.SHORTENED_URL_REACHED_N_VIEWS,
                new NotificationParams.ShortenedUrlReachedNViewsParams(uid, 10L),
                userId,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
        var repo = mock(NotificationRepository.class);
        when(repo.getNotificationById(notification.id(), userId)).thenReturn(Optional.of(notification));
        when(repo.setNotificationReadAtByIdAndUserId(any(OffsetDateTime.class), eq(notification.id()), eq(userId))).thenReturn(0);
        var service = new NotificationServiceImpl(repo);

        var result = service.readNotification(userId, notification.id());

        verify(repo).getNotificationById(notification.id(), userId);
        verify(repo).setNotificationReadAtByIdAndUserId(any(OffsetDateTime.class), eq(notification.id()), eq(userId));
        assert result.get() instanceof NotificationIsAlreadyRead;
    }
}
