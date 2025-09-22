package org.acme.domain.service;

import io.vavr.Tuple2;
import io.vavr.control.Either;
import org.acme.domain.command.CreateShortenedUrlCommand;
import org.acme.domain.command.UpdateOriginalUrlCommand;
import org.acme.domain.entity.ShortenedUrl;
import org.acme.domain.exceptions.url.ShortenUrlError;
import org.acme.domain.exceptions.url.UpdateOriginalUrlError;
import org.acme.domain.query.AvailableShortenedUrlWithAccessCount;
import org.acme.domain.repo.SaveShortenedUrlError;
import org.jspecify.annotations.NonNull;

import java.util.List;

public interface ShortenedUrlService {
    Either<ShortenUrlError, ShortenedUrl> generateShortenedUrl(@NonNull CreateShortenedUrlCommand command) throws SaveShortenedUrlError;

    Tuple2<Long, List<AvailableShortenedUrlWithAccessCount>> listOfAvailableUrls(
            @NonNull Integer page,
            @NonNull Integer size,
            boolean isAscending
    );

    Either<UpdateOriginalUrlError, ShortenedUrl> updateOriginalUrl(@NonNull UpdateOriginalUrlCommand command);
}
