package org.acme.domain.repo;

import io.vavr.Tuple2;
import org.acme.domain.entity.ShortenedUrl;
import org.acme.domain.query.AvailableShortenedUrlWithAccessCount;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;

public interface ShortenedUrlRepository {
    void insertShortenedUrl(@NonNull ShortenedUrl shortenedUrl) throws SaveShortenedUrlError;

    Optional<ShortenedUrl> getShortenedUrl(@NonNull String uniqueIdentifier);

    Tuple2<Long, List<AvailableShortenedUrlWithAccessCount>> listAvailableShortenedUrls(@NonNull Integer page, @NonNull Integer size, boolean isAscending);
}
