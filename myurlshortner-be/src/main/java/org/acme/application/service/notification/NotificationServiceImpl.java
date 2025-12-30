package org.acme.application.service.notification;

import io.vavr.control.Option;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.acme.application.repo.notification.NotificationRepository;
import org.acme.domain.entity.Notification;
import org.acme.domain.exceptions.DomainException;
import org.acme.domain.exceptions.NotificationIsAlreadyRead;
import org.acme.domain.exceptions.NotificationIsNotFound;
import org.acme.domain.service.NotificationService;
import org.jspecify.annotations.NonNull;

import java.time.OffsetDateTime;
import java.util.List;

@ApplicationScoped
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public List<Notification> getLatestNotifications(@NonNull Long userId) {
        return this.notificationRepository.getLatestNotificationsByUserId(userId, 5);
    }

    @Override
    @Transactional
    public Option<DomainException> readNotification(@NonNull Long userId, @NonNull Long notificationId) {
        var maybeNotification = this.notificationRepository.getNotificationById(notificationId, userId);
        if (maybeNotification.isPresent()) {
            if (this.notificationRepository.setNotificationReadAtByIdAndUserId(OffsetDateTime.now(), notificationId, userId) == 0) {
                return Option.of(new NotificationIsAlreadyRead());
            } else {
                return Option.none();
            }
        } else {
            return Option.of(new NotificationIsNotFound());
        }
    }
}
