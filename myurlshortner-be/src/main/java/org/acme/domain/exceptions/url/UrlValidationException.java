package org.acme.domain.exceptions.url;

public abstract class UrlValidationException extends Exception {

    public static class UniqueIdentifierCannotBeEmptyValidationException extends UrlValidationException {

    }

    public static class UserAgentCannotBeEmptyValidationException extends UrlValidationException {

    }

    public static class UniqueIdentifierIsTooLongValidationException extends UrlValidationException {

    }
}
