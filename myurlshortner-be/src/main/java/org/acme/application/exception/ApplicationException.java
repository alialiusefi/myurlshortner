package org.acme.application.exception;

public abstract class ApplicationException extends Exception {
    public String code;
    public String message;

    protected ApplicationException(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
