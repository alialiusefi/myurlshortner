package org.acme.domain.query;

import org.acme.domain.ShortenedUrl;

public record AvailableShortenedUrlWithAccessCount(
        ShortenedUrl shortenedUrl,
        Long accessCount
) {
}
