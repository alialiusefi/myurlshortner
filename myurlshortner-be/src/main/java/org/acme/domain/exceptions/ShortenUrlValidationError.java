package org.acme.domain.exceptions;

import java.util.List;

public class ShortenUrlValidationError extends Exception {
    private List<ShortenUrlException> errors;

    public ShortenUrlValidationError(List<ShortenUrlException> errors) {
        this.errors = errors;
    }

    public List<ShortenUrlException> getErrors() {
        return errors;
    }
}
