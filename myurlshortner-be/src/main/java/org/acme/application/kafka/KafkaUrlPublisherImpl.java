package org.acme.application.kafka;

import com.acme.events.ShortenedUrlUserEvents;
import com.acme.events.UserAccessedShortenedUrl;
import com.acme.events.UserCreatedShortenedUrl;
import io.quarkus.arc.profile.IfBuildProfile;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.inject.Singleton;
import org.acme.domain.entity.ShortenedUrl;
import org.acme.domain.events.V1UserCreatedShortenedUrlEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jspecify.annotations.NonNull;

import java.net.URI;
import java.net.URL;
import java.time.OffsetDateTime;

@IfBuildProfile(anyOf = {"dev", "prod"})
@Singleton
public class KafkaUrlPublisherImpl implements KafkaUrlPublisher {
    final MutinyEmitter<ShortenedUrlUserEvents> emitter;
    final String hostname;

    KafkaUrlPublisherImpl(
            @Channel("shortened-url-user-events") MutinyEmitter<ShortenedUrlUserEvents> emitter,
            @ConfigProperty(name = "app.hostname") String hostname
    ) {
        this.emitter = emitter;
        this.hostname = hostname;
    }

    @Override
    public void publishUserAccessedShortenedUrl(
            @NonNull ShortenedUrl shortenedUrl,
            @NonNull String userAgent,
            @NonNull OffsetDateTime accessedAt
    ) {
        var event = UserAccessedShortenedUrl.newBuilder()
                .setOriginalUrl(shortenedUrl.getOriginalUrl().toString())
                .setUserAgent(userAgent)
                .setShortenedUrl(shortenedUrl.shortenedUrl(hostname))
                .setUniqueIdentifier(shortenedUrl.getPublicIdentifier())
                .setAccessedAt(accessedAt.toString())
                .build();

        emitter.sendAndAwait(ShortenedUrlUserEvents.newBuilder().setUserAccessedShortenedUrlEvent(event).build());
    }

    public void publishUserCreatedShortenedUrl(
            @NonNull OffsetDateTime createdAt,
            @NonNull URI originalUrl,
            @NonNull String uniqueIdentifier
    ) {
        emitter.sendAndAwait(
                ShortenedUrlUserEvents.newBuilder()
                        .setUserCreatedShortenedUrlEvent(
                                UserCreatedShortenedUrl.newBuilder()
                                        .setCreatedAt(createdAt.toString())
                                        .setOriginalUrl(originalUrl.toString())
                                        .setUniqueIdentifier(uniqueIdentifier)
                                        .build()
                        )
                        .build()
        );
    }
}
