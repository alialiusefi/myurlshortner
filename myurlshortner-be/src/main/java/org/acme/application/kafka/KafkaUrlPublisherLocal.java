package org.acme.application.kafka;

import io.quarkus.arc.profile.IfBuildProfile;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.domain.entity.ShortenedUrl;
import org.acme.domain.events.V1UserCreatedShortenedUrlEvent;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URL;
import java.time.OffsetDateTime;

@ApplicationScoped
@IfBuildProfile(anyOf = {"local", "test"})
public class KafkaUrlPublisherLocal implements KafkaUrlPublisher {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void publishUserAccessedShortenedUrl(@NonNull ShortenedUrl shortenedUrl, @NonNull String userAgent, @NonNull OffsetDateTime accessedAt) {
        logger.info("Message sent successfully!");
    }

    @Override
    public void publishUserCreatedShortenedUrl(
            @NonNull OffsetDateTime createdAt,
            @NonNull URI originalUrl,
            @NonNull String uniqueIdentifier
    ) {
        logger.info("Message sent successfully!");
    }
}
