package org.acme.application.exception.url;

import org.acme.domain.exceptions.DomainException;
import org.acme.domain.exceptions.ShortenedUrlIsNotFoundException;

import java.util.Optional;

public class GetShortenedUrlError extends Exception {
    public Optional<ShortenedUrlIsNotFoundException> notFound;
    public Optional<? extends DomainException> validationException;

    public GetShortenedUrlError(ShortenedUrlIsNotFoundException ex) {
        this.notFound = Optional.of(ex);
        this.validationException = Optional.empty();
    }

    public GetShortenedUrlError(DomainException validationException) {
        this.validationException = Optional.of(validationException);
        this.notFound = Optional.empty();
    }
}
