package org.acme.domain.exceptions.url;

import org.acme.domain.exceptions.DomainException;

public abstract class GetUrlValidationException extends DomainException {

    GetUrlValidationException(String code, String message) {
        super(code, message);
    }

    public static class UniqueIdentifierCannotBeEmptyValidationExceptionGet extends GetUrlValidationException {

        public UniqueIdentifierCannotBeEmptyValidationExceptionGet() {
            super("UNIQUE_IDENTIFIER_CANNOT_BE_EMPTY", "The unique identifier cannot be empty.");
        }
    }

    public static class UserAgentCannotBeEmptyValidationExceptionGet extends GetUrlValidationException {

        public UserAgentCannotBeEmptyValidationExceptionGet() {
            super("USER_AGENT_CANNOT_BE_EMPTY", "The user agent cannot be empty.");
        }
    }

    public static class UniqueIdentifierIsTooLongValidationExceptionGet extends GetUrlValidationException {

        public UniqueIdentifierIsTooLongValidationExceptionGet() {
            super("UNIQUE_IDENTIFIER_IS_TOO_LONG", "Unique identifier is too long.");
        }
    }
}
