package org.acme.domain.events;

import org.jspecify.annotations.NonNull;

import java.net.URI;
import java.time.OffsetDateTime;

public record V1UserGiftedShortenedUrlEvent(
        @NonNull String uniqueIdentifier,
        @NonNull OffsetDateTime createdAt,
        @NonNull URI originalUrl,
        @NonNull Long sourceUserId,
        @NonNull Long targetUserId
) implements ShortenedUrlEvent {
}
