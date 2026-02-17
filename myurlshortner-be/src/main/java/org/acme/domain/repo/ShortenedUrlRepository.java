package org.acme.domain.repo;

import org.acme.application.repo.exception.ShortenedUrlOptimisticLockException;
import org.acme.domain.entity.ShortenedUrl;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface ShortenedUrlRepository {
    void insertShortenedUrl(@NonNull ShortenedUrl shortenedUrl) throws SaveShortenedUrlConflictError;

    Optional<ShortenedUrl> getShortenedUrl(@NonNull String uniqueIdentifier, @Nullable Long userId);

    void updateShortenedUrl(@NonNull ShortenedUrl shortenedUrl, OffsetDateTime existingUpdatedAt) throws ShortenedUrlOptimisticLockException;
}
