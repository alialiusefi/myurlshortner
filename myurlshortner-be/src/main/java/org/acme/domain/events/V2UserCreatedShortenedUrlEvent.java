package org.acme.domain.events;

import org.jspecify.annotations.NonNull;

import java.net.URI;
import java.time.OffsetDateTime;

public record V2UserCreatedShortenedUrlEvent(
        @NonNull String uniqueIdentifier,
        @NonNull OffsetDateTime createdAt,
        @NonNull Boolean isEnabled,
        @NonNull URI originalUrl,
        @NonNull Long userId,
        @NonNull String title
) implements ShortenedUrlEvent {
}
