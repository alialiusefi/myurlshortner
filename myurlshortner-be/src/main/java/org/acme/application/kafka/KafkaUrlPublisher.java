package org.acme.application.kafka;

import org.acme.domain.entity.ShortenedUrl;
import org.acme.domain.events.V4UserCreatedShortenedUrlEvent;
import org.acme.domain.events.V5UserUpdatedOriginalUrlEvent;
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

    void publishUserUpdatedOriginalUrl(
            @NonNull V5UserUpdatedOriginalUrlEvent event
    );
}
