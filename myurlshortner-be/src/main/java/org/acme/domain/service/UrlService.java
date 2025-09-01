package org.acme.domain.service;

import io.vavr.Tuple2;
import io.vavr.control.Either;
import org.acme.domain.ShortenedUrl;
import org.acme.domain.exceptions.url.GetUrlError;
import org.jspecify.annotations.NonNull;

import java.net.URI;
import java.util.List;

public interface UrlService {
    Either<GetUrlError, URI> getUrl(
            @NonNull String uniqueIdentifier,
            @NonNull String userAgent
    );

    Tuple2<Long, List<ShortenedUrl>> listOfAvailableUrls(
            @NonNull Integer page,
            @NonNull Integer size
    );
}
