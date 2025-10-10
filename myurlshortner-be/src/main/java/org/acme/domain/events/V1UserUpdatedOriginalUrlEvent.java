package org.acme.domain.events;

import org.jspecify.annotations.NonNull;

import java.net.URI;
import java.time.OffsetDateTime;

public record V1UserUpdatedOriginalUrlEvent(
        @NonNull String uniqueIdentifier,
        @NonNull URI newOriginalUrl,
        @NonNull OffsetDateTime updatedAt
) implements ShortenedUrlEvent {
}
