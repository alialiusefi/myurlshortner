package org.acme.domain.exceptions;

import java.util.List;

public class ShortenedUrlValidationError extends Exception {
    private List<ShortenUrlException> errors;

    public ShortenedUrlValidationError(List<ShortenUrlException> errors) {
        this.errors = errors;
    }

    public List<ShortenUrlException> getErrors() {
        return errors;
    }
}
