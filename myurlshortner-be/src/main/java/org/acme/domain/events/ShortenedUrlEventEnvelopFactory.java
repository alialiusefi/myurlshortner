package org.acme.domain.events;

import org.acme.domain.entity.ShortenedUrl;

import java.util.UUID;

public class ShortenedUrlEventEnvelopFactory {
    public static ShortenedUrlEventEnvelop<V1UserCreatedShortenedUrlEvent> createV4CreatedShortenUrlEvent(
            ShortenedUrl shortenedUrl
    ) {
        return new ShortenedUrlEventEnvelop<>(
                UUID.randomUUID(),
                1,
                ShortenedUrlRecordType.USER_CREATED_SHORTENED_URL,
                shortenedUrl.getCreatedAt(),
                new V1UserCreatedShortenedUrlEvent(shortenedUrl.getPublicIdentifier(), shortenedUrl.getCreatedAt(), shortenedUrl.isEnabled(), shortenedUrl.getOriginalUrl())
        );
    }

    public static ShortenedUrlEventEnvelop<V1UserUpdatedOriginalUrlEvent> createV5UpdatedOriginalUrlEvent(
            ShortenedUrl url
    ) {
        return new ShortenedUrlEventEnvelop<>(
                UUID.randomUUID(),
                1,
                ShortenedUrlRecordType.USER_UPDATED_ORIGINAL_URL,
                url.getUpdatedAt(),
                new V1UserUpdatedOriginalUrlEvent(url.getPublicIdentifier(), url.getOriginalUrl(), url.getUpdatedAt())
        );
    }
}
