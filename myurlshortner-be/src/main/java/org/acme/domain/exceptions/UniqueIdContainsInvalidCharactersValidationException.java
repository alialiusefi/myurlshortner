package org.acme.domain.exceptions;

public class UniqueIdContainsInvalidCharactersValidationException extends DomainException {
    public UniqueIdContainsInvalidCharactersValidationException() {
        super("UNIQUE_ID_CONTAINS_INVALID_CHARACTERS", "Unique identifier contains invalid characters.");
    }
}
