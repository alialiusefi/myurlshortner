package org.acme.application.kafka;

import org.acme.domain.entity.ShortenedUrl;
import org.acme.domain.events.V4UserCreatedShortenedUrlEvent;
import org.jspecify.annotations.NonNull;

import java.time.OffsetDateTime;

public interface KafkaUrlPublisher {

    void publishUserAccessedShortenedUrl(
            @NonNull ShortenedUrl shortenedUrl,
            @NonNull String userAgent,
            @NonNull OffsetDateTime accessedAt
    );

    void publishUserCreatedShortenedUrl(
            @NonNull V4UserCreatedShortenedUrlEvent event
    );
}
