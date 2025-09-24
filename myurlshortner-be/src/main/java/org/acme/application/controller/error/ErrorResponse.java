package org.acme.application.controller.error;

import org.acme.application.exception.ApplicationException;
import org.acme.domain.exceptions.DomainException;

import java.util.List;

public class ErrorResponse {
    private List<Error> errors;

    public ErrorResponse(List<Error> errors) {
        this.errors = errors;
    }

    public static ErrorResponse buildFromDomainErrors(List<? extends DomainException> errors) {
        return new ErrorResponse(errors.stream().map(a -> new Error(a.code, a.message)).toList());
    }

    public static ErrorResponse buildFromApplicationErrors(List<? extends ApplicationException> errors) {
        return new ErrorResponse(errors.stream().map(a -> new Error(a.code, a.message)).toList());
    }

    public record Error(String code, String message) {
    }

    public List<Error> getErrors() {
        return errors;
    }
}
