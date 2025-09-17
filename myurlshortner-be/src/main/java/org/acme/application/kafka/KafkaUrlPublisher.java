package org.acme.application.kafka;

import org.acme.domain.ShortenedUrl;
import org.jspecify.annotations.NonNull;

import java.time.OffsetDateTime;

public interface KafkaUrlPublisher {

    void publishUserAccessedShortenedUrl(
            @NonNull ShortenedUrl shortenedUrl,
            @NonNull String userAgent,
            @NonNull OffsetDateTime accessedAt
    );
}
