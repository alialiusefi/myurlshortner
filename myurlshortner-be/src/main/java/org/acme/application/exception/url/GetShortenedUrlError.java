package org.acme.application.exception.url;

import org.acme.domain.exceptions.DomainException;
import org.acme.domain.exceptions.ShortenedUrlIsNotFoundException;

import java.util.List;
import java.util.Optional;

public class GetShortenedUrlError extends Exception {
    public Optional<ShortenedUrlIsNotFoundException> notFound;
    public List<? extends DomainException> validationException;

    public GetShortenedUrlError(ShortenedUrlIsNotFoundException ex) {
        this.notFound = Optional.of(ex);
        this.validationException = List.of();
    }

    public GetShortenedUrlError(List<DomainException> validationException) {
        this.validationException = (validationException);
        this.notFound = Optional.empty();
    }
}
