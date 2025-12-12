package org.acme.application.service.notification;

import io.vavr.control.Option;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.acme.application.repo.notification.NotificationRepository;
import org.acme.domain.entity.Notification;
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
    public Option<NotificationIsNotFound> readNotification(@NonNull Long userId, @NonNull Long notificationId) {
        if (this.notificationRepository.setNotificationReadAtByIdAndUserId(OffsetDateTime.now(), notificationId, userId) == 0) {
            return Option.of(new NotificationIsNotFound());
        } else {
            return Option.none();
        }
    }
}
