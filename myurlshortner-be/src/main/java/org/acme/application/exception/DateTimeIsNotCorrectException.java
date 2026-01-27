package org.acme.application.exception;

public class DateTimeIsNotCorrectException extends ApplicationException {
    public DateTimeIsNotCorrectException(String inputParam) {
        super("DATETIME_PARAM_IS_NOT_CORRECT", String.format("The provided datetime param '%s' is not correct", inputParam));
    }
}
