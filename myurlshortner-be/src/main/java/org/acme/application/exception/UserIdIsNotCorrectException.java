package org.acme.application.exception;

import org.acme.domain.exceptions.DomainException;

public class UserIdIsNotCorrectException extends DomainException {
    public UserIdIsNotCorrectException(String value) {
        super("USER_ID_IS_NOT_CORRECT", String.format("The provided user id value %s is not correct. It must be a number above 0", value));
    }
}
