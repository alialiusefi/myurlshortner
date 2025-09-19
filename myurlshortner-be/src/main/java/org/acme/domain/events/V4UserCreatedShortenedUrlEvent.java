package org.acme.domain.events;

import java.net.URI;
import java.time.OffsetDateTime;

public record V4UserCreatedShortenedUrlEvent(
        String uniqueIdentifier,
        OffsetDateTime createdAt,
        URI originalUrl
) implements ShortenedUrlEvent {
}
