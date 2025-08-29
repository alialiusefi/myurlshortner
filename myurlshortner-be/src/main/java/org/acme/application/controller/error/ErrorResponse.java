package org.acme.application.controller.error;

import org.acme.domain.exceptions.DomainException;

import java.util.List;

public class ErrorResponse {
    private List<Error> errors;

    public ErrorResponse(List<? extends DomainException> errors) {
        this.errors = errors.stream().map(a -> new Error(a.code, a.message)).toList();
    }

    public record Error(String code, String message) {
    }

    public List<Error> getErrors() {
        return errors;
    }
}
