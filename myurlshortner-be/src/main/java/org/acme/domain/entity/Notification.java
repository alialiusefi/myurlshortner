package org.acme.domain.entity;

import java.time.OffsetDateTime;

public record Notification(
        Long id,
        String uniqueIdentifier,
        NotificationType type,
        NotificationParams params,
        Long userId,
        OffsetDateTime createdAt,
        OffsetDateTime readAt
) {}
