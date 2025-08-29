package org.acme.application.kafka;

import com.acme.events.UserAccessedShortenedUrl;
import io.quarkus.arc.profile.IfBuildProfile;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.inject.Singleton;
import org.acme.domain.ShortenedUrl;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jspecify.annotations.NonNull;

@IfBuildProfile(allOf = {"dev", "prod"})
@Singleton
public class KafkaUrlPublisherImpl implements KafkaUrlPublisher {
    final MutinyEmitter<UserAccessedShortenedUrl> emitter;

    KafkaUrlPublisherImpl(
            @Channel("shortened-url-events") MutinyEmitter<UserAccessedShortenedUrl> emitter
    ) {
        this.emitter = emitter;
    }

    @Override
    public void publishUserAccessedShortenedUrl(
            @NonNull ShortenedUrl shortenedUrl,
            @NonNull String userAgent
    ) {
        var event = UserAccessedShortenedUrl.newBuilder()
                .setOriginalUrl(shortenedUrl.getOriginalUrl().toString())
                .setUserAgent(userAgent)
                .setUniqueIdentifier(shortenedUrl.getPublicIdentifier())
                .build();

        emitter.sendAndAwait(event);
    }
}
