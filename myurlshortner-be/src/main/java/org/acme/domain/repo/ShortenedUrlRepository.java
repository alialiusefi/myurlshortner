package org.acme.domain.repo;

import io.vavr.Tuple2;
import org.acme.domain.ShortenedUrl;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ShortenedUrlRepository {
    void insertShortenedUrl(@NonNull ShortenedUrl shortenedUrl) throws SaveShortenedUrlError;

    @Nullable
    ShortenedUrl getShortenedUrl(@NonNull String uniqueIdentifier);

    Tuple2<Long, List<ShortenedUrl>> listAvailableShortenedUrls(@NonNull Integer page, @NonNull Integer size);
}
