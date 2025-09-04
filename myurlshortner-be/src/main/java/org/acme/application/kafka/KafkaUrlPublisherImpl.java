package org.acme.application.kafka;

import com.acme.events.ShortenedUrlUserEvents;
import com.acme.events.UserAccessedShortenedUrl;
import io.quarkus.arc.profile.IfBuildProfile;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.inject.Singleton;
import org.acme.domain.ShortenedUrl;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jspecify.annotations.NonNull;

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
            @NonNull String userAgent
    ) {
        var event = UserAccessedShortenedUrl.newBuilder()
                .setOriginalUrl(shortenedUrl.getOriginalUrl().toString())
                .setUserAgent(userAgent)
                .setShortenedUrl(shortenedUrl.shortenedUrl(hostname))
                .build();

        emitter.sendAndAwait(ShortenedUrlUserEvents.newBuilder().setUserAccessedShortenedUrlEvent(event).build());
    }
}
