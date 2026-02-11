package org.acme.domain.events;

import org.jspecify.annotations.NonNull;

import java.time.OffsetDateTime;

public record V1UserUpdatedTitleEvent(
        @NonNull String uniqueIdentifier,
        @NonNull String newTitle,
        @NonNull OffsetDateTime createdAt,
        @NonNull Long userId
) implements ShortenedUrlEvent {

}
