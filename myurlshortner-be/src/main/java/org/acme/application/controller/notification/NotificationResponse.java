package org.acme.application.controller.notification;

import org.acme.domain.entity.NotificationParams;
import org.acme.domain.entity.NotificationType;

import java.time.OffsetDateTime;
import java.util.List;

public record NotificationResponse(
        List<NotificationRowResponse> data
) {
    public record NotificationRowResponse(
            Long id,
            NotificationType type,
            NotificationParams params,
            OffsetDateTime readAt
    ) {
    }
}
