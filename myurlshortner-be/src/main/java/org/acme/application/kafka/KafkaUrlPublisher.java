package org.acme.application.kafka;

import org.acme.domain.ShortenedUrl;
import org.jspecify.annotations.NonNull;

public interface KafkaUrlPublisher {

    void publishUserAccessedShortenedUrl(
            @NonNull ShortenedUrl shortenedUrl,
            @NonNull String userAgent
    );
}
