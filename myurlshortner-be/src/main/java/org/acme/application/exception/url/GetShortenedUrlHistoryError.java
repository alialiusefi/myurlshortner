package org.acme.application.exception.url;

import org.acme.application.exception.ApplicationException;
import org.acme.domain.exceptions.ShortenedUrlIsNotFoundException;

import java.util.List;
import java.util.Optional;


public class GetShortenedUrlHistoryError extends Exception {
    public List<? extends ApplicationException> errors;
    public Optional<ShortenedUrlIsNotFoundException> error;

    public GetShortenedUrlHistoryError(
            List<ApplicationException> appErrors
    ) {
        this.errors = appErrors;
        this.error = Optional.empty();
    }

    public GetShortenedUrlHistoryError(
            ShortenedUrlIsNotFoundException error
    ) {
        this.error = Optional.of(error);
        this.errors = List.of();
    }
}
