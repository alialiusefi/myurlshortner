package org.acme.application.exception;

public class OffsetIsNotCorrectException extends ApplicationException {
    public OffsetIsNotCorrectException(Integer input) {
        super("OFFSET_PARAM_IS_NOT_CORRECT", String.format("The provided offset param '%s' is not correct", input));
    }
}
