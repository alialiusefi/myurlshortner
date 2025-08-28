package org.acme.domain.exceptions;

public class DomainException extends Exception {
    public String code;
    public String message;

    protected DomainException(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
