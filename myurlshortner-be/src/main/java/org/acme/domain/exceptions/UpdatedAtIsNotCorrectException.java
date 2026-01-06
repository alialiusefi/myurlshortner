package org.acme.domain.exceptions;

public class UpdatedAtIsNotCorrectException extends DomainException {

    public UpdatedAtIsNotCorrectException(String input) {
        super("UPDATED_AT_IS_NOT_CORRECT", String.format("The provided updated_at datetime '%s' is not correct", input));
    }
}
