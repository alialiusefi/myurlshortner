package org.acme.application.exception;

public class PageSizeIsNotCorrectException extends ApplicationException {
    public PageSizeIsNotCorrectException(Integer size) {
        super("SIZE_QUERY_PARAM_IS_NOT_CORRECT", String.format("The provided size '%s' should be from 1 to 100.", size));
    }
}
