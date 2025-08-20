package org.acme.domain.repo;

import org.acme.domain.ShortenedUrl;

public interface ShortenedUrlRepository {
    void insertShortenedUrl(ShortenedUrl shortenedUrl) throws SaveShortenedUrlError;
}
