package org.acme.domain.service;

import io.vavr.Tuple2;
import io.vavr.control.Either;
import org.acme.domain.command.CreateShortenedUrlCommand;
import org.acme.domain.command.UpdateOriginalUrlCommand;
import org.acme.domain.entity.ShortenedUrl;
import org.acme.domain.events.ShortenedUrlEvent;
import org.acme.domain.exceptions.url.ShortenUrlError;
import org.acme.domain.exceptions.url.UpdateOriginalUrlError;
import org.acme.domain.projection.AvailableShortenedUrl;
import org.acme.domain.repo.SaveShortenedUrlError;
import org.jspecify.annotations.NonNull;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface ShortenedUrlService {
    Optional<ShortenedUrl> getShortenedUrl(@NonNull String uniqueIdentifier);

    Optional<ShortenedUrl> getShortenedUrlFromEvents(@NonNull String uniqueIdentifier);

    Either<ShortenUrlError, ShortenedUrl> generateShortenedUrl(@NonNull CreateShortenedUrlCommand command) throws SaveShortenedUrlError;

    Tuple2<Long, List<AvailableShortenedUrl>> listOfAvailableUrls(
            @NonNull Integer page,
            @NonNull Integer size,
            boolean isAscending
    );

    Either<UpdateOriginalUrlError, ShortenedUrl> updateOriginalUrl(@NonNull UpdateOriginalUrlCommand command);

    List<? extends ShortenedUrlEvent> getShortenedUrlHistory(
            @NonNull String uniqueIdentifier,
            @NonNull Integer offset,
            @NonNull Integer size,
            @NonNull OffsetDateTime from
    );
}
