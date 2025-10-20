package org.acme.application.exception;

import org.acme.domain.exceptions.DomainException;

public class ApplicationException extends Exception {
    public String code;
    public String message;

    protected ApplicationException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApplicationException(DomainException domainException) {
        super(domainException);
        this.code = domainException.code;
        this.message = domainException.message;
    }
}
