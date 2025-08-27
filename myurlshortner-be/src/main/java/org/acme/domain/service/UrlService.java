package org.acme.domain.service;

import io.vavr.control.Either;
import org.acme.domain.exceptions.url.GetUrlError;
import org.jspecify.annotations.NonNull;

import java.net.URI;

public interface UrlService {
    Either<GetUrlError, URI> getUrl(
            @NonNull String uniqueIdentifier,
            @NonNull String userAgent
    );
}
