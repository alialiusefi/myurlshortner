package org.acme.domain.query;

import org.acme.domain.entity.ShortenedUrl;

public record AvailableShortenedUrlWithAccessCount(
        ShortenedUrl shortenedUrl,
        Long accessCount
) {
}
