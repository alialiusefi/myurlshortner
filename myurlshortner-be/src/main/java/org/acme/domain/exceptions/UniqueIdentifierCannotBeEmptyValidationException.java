package org.acme.domain.exceptions;

public class UniqueIdentifierCannotBeEmptyValidationException extends DomainException {
    public UniqueIdentifierCannotBeEmptyValidationException() {
        super("UNIQUE_IDENTIFIER_CANNOT_BE_EMPTY", "The unique identifier cannot be empty.");
    }
}
