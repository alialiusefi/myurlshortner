package org.acme.application.exception.url;

import org.acme.application.exception.ApplicationException;

import java.util.List;

public class GetAvailableUrlsError extends Exception {
    private final List<? extends ApplicationException> errors;

    public GetAvailableUrlsError(List<? extends ApplicationException> errors) {
        this.errors = errors;
    }

    public List<? extends ApplicationException> getErrors() {
        return errors;
    }
}
