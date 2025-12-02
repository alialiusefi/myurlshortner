package org.acme.domain.repo;

import io.vavr.Tuple2;
import org.acme.application.repo.exception.ShortenedUrlOptimisticLockException;
import org.acme.domain.entity.ShortenedUrl;
import org.acme.domain.projection.AvailableShortenedUrl;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface ShortenedUrlRepository {
    void insertShortenedUrl(@NonNull ShortenedUrl shortenedUrl) throws SaveShortenedUrlConflictError;

    Optional<ShortenedUrl> getShortenedUrl(@NonNull String uniqueIdentifier, @Nullable Long userId);

    Tuple2<Long, List<AvailableShortenedUrl>> listAvailableShortenedUrls(
            @NonNull Integer page,
            @NonNull Integer size,
            boolean isAscending,
            @Nullable Long userId
    );

    void updateShortenedUrl(@NonNull ShortenedUrl shortenedUrl, OffsetDateTime existingUpdatedAt) throws ShortenedUrlOptimisticLockException;
}
