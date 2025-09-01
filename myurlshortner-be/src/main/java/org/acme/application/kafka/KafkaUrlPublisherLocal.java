package org.acme.application.kafka;

import io.quarkus.arc.profile.IfBuildProfile;
import jakarta.inject.Singleton;
import org.acme.domain.ShortenedUrl;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@IfBuildProfile("local")
public class KafkaUrlPublisherLocal implements KafkaUrlPublisher {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void publishUserAccessedShortenedUrl(@NonNull ShortenedUrl shortenedUrl, @NonNull String userAgent) {
        logger.info("Message sent successfully!");
    }
}
