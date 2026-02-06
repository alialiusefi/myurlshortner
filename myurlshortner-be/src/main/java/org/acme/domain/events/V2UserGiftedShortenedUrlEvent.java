package org.acme.domain.events;

import org.jspecify.annotations.NonNull;

import java.net.URI;
import java.time.OffsetDateTime;

public record V2UserGiftedShortenedUrlEvent(
        @NonNull String uniqueIdentifier,
        @NonNull OffsetDateTime createdAt,
        @NonNull URI originalUrl,
        @NonNull Long sourceUserId,
        @NonNull Long targetUserId,
        @NonNull String title
) implements ShortenedUrlEvent {
}
