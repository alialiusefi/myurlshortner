package org.acme.domain.events;

import org.acme.domain.entity.ShortenedUrl;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;

public class ShortenedUrlEventEnvelopFactory {
    public static ShortenedUrlEventEnvelop<V2UserCreatedShortenedUrlEvent> createV2CreatedShortenUrlEvent(
            ShortenedUrl shortenedUrl
    ) {
        return new ShortenedUrlEventEnvelop<>(
                UUID.randomUUID(),
                2,
                ShortenedUrlRecordType.USER_CREATED_SHORTENED_URL,
                shortenedUrl.getCreatedAt(),
                new V2UserCreatedShortenedUrlEvent(
                        shortenedUrl.getPublicIdentifier(),
                        shortenedUrl.getCreatedAt(),
                        shortenedUrl.isEnabled(),
                        shortenedUrl.getOriginalUrl(),
                        shortenedUrl.getUserId(),
                        shortenedUrl.getTitle()
                )
        );
    }

    public static ShortenedUrlEventEnvelop<V1UserUpdatedOriginalUrlEvent> createV1UpdatedOriginalUrlEvent(
            ShortenedUrl url,
            URI newTargetUrl
    ) {
        var createdAt = OffsetDateTime.now();
        return new ShortenedUrlEventEnvelop<>(
                UUID.randomUUID(),
                1,
                ShortenedUrlRecordType.USER_UPDATED_ORIGINAL_URL,
                createdAt,
                new V1UserUpdatedOriginalUrlEvent(
                        url.getPublicIdentifier(),
                        newTargetUrl,
                        createdAt,
                        url.getUserId()
                )
        );
    }

    public static ShortenedUrlEventEnvelop<V2UserGiftedShortenedUrlEvent> createV2CreateUserGiftedShortenedUrlEvent(
            ShortenedUrl url,
            Long targetUserId
    ) {
        var newCreatedAt = OffsetDateTime.now();
        return new ShortenedUrlEventEnvelop<>(
                UUID.randomUUID(),
                2,
                ShortenedUrlRecordType.USER_GIFTED_SHORTENED_URL,
                newCreatedAt,
                new V2UserGiftedShortenedUrlEvent(
                        url.getPublicIdentifier(),
                        newCreatedAt,
                        url.getOriginalUrl(),
                        url.getUserId(),
                        targetUserId,
                        url.getTitle()
                )
        );
    }

    public static ShortenedUrlEventEnvelop<V1UserUpdatedTitleEvent> createV1UpdatedTitleEvent(
            ShortenedUrl url,
            String title
    ) {
        var newCreatedAt = OffsetDateTime.now();
        return new ShortenedUrlEventEnvelop<>(
                UUID.randomUUID(),
                1,
                ShortenedUrlRecordType.USER_UPDATED_TITLE,
                newCreatedAt,
                new V1UserUpdatedTitleEvent(
                        url.getPublicIdentifier(),
                        title,
                        newCreatedAt,
                        url.getUserId()
                )
        );
    }
}
