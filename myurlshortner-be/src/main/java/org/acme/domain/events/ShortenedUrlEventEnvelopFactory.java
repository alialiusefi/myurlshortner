package org.acme.domain.events;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;

public class ShortenedUrlEventEnvelopFactory {
    public static ShortenedUrlEventEnvelop<V4UserCreatedShortenedUrlEvent> createV4CreatedShortenUrlEvent(
            String uniqueIdentifier,
            OffsetDateTime createdAt,
            URI originalUrl
    ) {
        var now = OffsetDateTime.now();
        return new ShortenedUrlEventEnvelop<>(
                UUID.randomUUID(),
                "com.acme.events.UserCreatedShortenedUrl",
                4,
                ShortenedUrlRecordType.USER_CREATED_SHORTENED_URL,
                createdAt,
                now,
                now,
                new V4UserCreatedShortenedUrlEvent(uniqueIdentifier, createdAt, originalUrl)
        );
    }

    public static ShortenedUrlEventEnvelop<V5UserUpdatedOriginalUrlEvent> createV5UpdatedOriginalUrlEvent(
            String uniqueIdentifier,
            URI newOriginalUrl,
            OffsetDateTime updatedAt
    ) {
        var now = OffsetDateTime.now();
        return new ShortenedUrlEventEnvelop<>(
                UUID.randomUUID(),
                "com.acme.events.UserUpdatedOriginalUrl",
                5,
                ShortenedUrlRecordType.USER_UPDATED_ORIGINAL_URL,
                updatedAt,
                now,
                now,
                new V5UserUpdatedOriginalUrlEvent(uniqueIdentifier, newOriginalUrl, updatedAt)
        );
    }
}
