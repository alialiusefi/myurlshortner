package org.acme.domain.exceptions;

public class UniqueIdentifierIsTooLongValidationException extends DomainException {
    public UniqueIdentifierIsTooLongValidationException() {
        super("UNIQUE_IDENTIFIER_IS_TOO_LONG", "Unique identifier is too long.");
    }
}
