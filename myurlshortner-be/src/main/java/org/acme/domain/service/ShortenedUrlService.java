package org.acme.domain.service;

import io.vavr.Tuple2;
import io.vavr.control.Either;
import org.acme.domain.ShortenedUrl;
import org.acme.domain.exceptions.url.ShortenUrlError;
import org.acme.domain.query.AvailableShortenedUrlWithAccessCount;
import org.acme.domain.repo.SaveShortenedUrlError;
import org.jspecify.annotations.NonNull;

import java.util.List;

public interface ShortenedUrlService {
    Either<ShortenUrlError, ShortenedUrl> generateShortenedUrl(String originalUrl) throws SaveShortenedUrlError;

    Tuple2<Long, List<AvailableShortenedUrlWithAccessCount>> listOfAvailableUrls(
            @NonNull Integer page,
            @NonNull Integer size,
            boolean isAscending
    );
}
