package org.acme.application.kafka;

import org.acme.domain.entity.ShortenedUrl;
import org.jspecify.annotations.NonNull;

import java.net.URI;
import java.time.OffsetDateTime;

public interface KafkaUrlPublisher {

    void publishUserAccessedShortenedUrl(
            @NonNull ShortenedUrl shortenedUrl,
            @NonNull String userAgent,
            @NonNull OffsetDateTime accessedAt
    );

    void publishUserCreatedShortenedUrl(
            @NonNull OffsetDateTime createdAt,
            @NonNull URI originalUrl,
            @NonNull String uniqueIdentifier
    );
}
