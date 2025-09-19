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
}
