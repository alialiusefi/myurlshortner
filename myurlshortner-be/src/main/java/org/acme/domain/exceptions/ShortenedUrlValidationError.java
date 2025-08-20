package org.acme.domain.exceptions;

import java.util.List;

public class ShortenedUrlValidationError extends Exception {
    private List<ShortenedUrlException> errors;

    public ShortenedUrlValidationError(List<ShortenedUrlException> errors) {
        this.errors = errors;
    }

    public List<ShortenedUrlException> getErrors() {
        return errors;
    }
}
