package org.acme.application.exception;

public class PageNumberIsNotCorrectException extends ApplicationException {
    public PageNumberIsNotCorrectException(Integer page) {
        super("PAGE_QUERY_PARAM_IS_NOT_CORRECT", String.format("The provided page '%s' should start from 1.", page));
    }
}
