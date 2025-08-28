package org.acme.domain.exceptions.url;

import org.acme.domain.exceptions.DomainException;

public abstract class UrlValidationException extends DomainException {

    UrlValidationException(String code, String message) {
        super(code, message);
    }

    public static class UniqueIdentifierCannotBeEmptyValidationException extends UrlValidationException {

        public UniqueIdentifierCannotBeEmptyValidationException() {
            super("UNIQUE_IDENTIFIER_CANNOT_BE_EMPTY", "The unique identifier cannot be empty.");
        }
    }

    public static class UserAgentCannotBeEmptyValidationException extends UrlValidationException {

        public UserAgentCannotBeEmptyValidationException() {
            super("USER_AGENT_CANNOT_BE_EMPTY", "The user agent cannot be empty.");
        }
    }

    public static class UniqueIdentifierIsTooLongValidationException extends UrlValidationException {

        public UniqueIdentifierIsTooLongValidationException() {
            super("UNIQUE_IDENTIFIER_IS_TOO_LONG", "Unique identifier is too long.");
        }
    }
}
