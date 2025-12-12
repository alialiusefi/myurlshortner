package org.acme.application.service.notification;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.application.repo.notification.NotificationRepository;
import org.acme.domain.entity.Notification;
import org.acme.domain.service.NotificationService;
import org.jspecify.annotations.NonNull;

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
}
