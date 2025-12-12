package org.acme.domain.service;

import io.vavr.control.Option;
import org.acme.domain.entity.Notification;
import org.acme.domain.exceptions.NotificationIsNotFound;
import org.jspecify.annotations.NonNull;

import java.util.List;

public interface NotificationService {
    List<Notification> getLatestNotifications(@NonNull Long userId);
    Option<NotificationIsNotFound> readNotification(@NonNull Long userId, @NonNull Long notificationId);
}
