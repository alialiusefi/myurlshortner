package org.acme.application.controller.url;

import java.time.OffsetDateTime;

public record ShortenedUrlResponse(
        String uniqueIdentifier,
        String shortenedUrl,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        String url,
        boolean isEnabled
) {
}
