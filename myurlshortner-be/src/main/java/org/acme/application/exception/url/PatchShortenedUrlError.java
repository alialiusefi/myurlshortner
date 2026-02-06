package org.acme.application.exception.url;

import org.acme.domain.exceptions.DomainException;
import org.acme.domain.exceptions.ShortenedUrlIsNotFoundException;

import java.util.List;
import java.util.Optional;

public record PatchShortenedUrlError(
        Optional<ShortenedUrlIsNotFoundException> notFound,
        List<DomainException> validationErrors
) {
}
