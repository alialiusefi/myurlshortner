package org.acme.domain.service;

import org.acme.domain.entity.Notification;
import org.jspecify.annotations.NonNull;

import java.util.List;

public interface NotificationService {
    List<Notification> getLatestNotifications(@NonNull Long userId);
}
