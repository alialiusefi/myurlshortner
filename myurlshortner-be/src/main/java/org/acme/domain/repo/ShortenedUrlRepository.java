package org.acme.domain.repo;

import org.acme.domain.ShortenedUrl;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface ShortenedUrlRepository {
    void insertShortenedUrl(ShortenedUrl shortenedUrl) throws SaveShortenedUrlError;

    @Nullable
    ShortenedUrl getShortenedUrl(@NonNull String uniqueIdentifier);
}
