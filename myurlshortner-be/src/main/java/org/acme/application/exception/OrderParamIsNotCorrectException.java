package org.acme.application.exception;

public class OrderParamIsNotCorrectException extends ApplicationException {
    public OrderParamIsNotCorrectException(String input) {
        super("ORDER_PARAM_IS_NOT_CORRECT", String.format("The provided order param '%s' is not correct", input));
    }
}
