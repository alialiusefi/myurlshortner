package org.acme.domain.exceptions.url;

import org.acme.domain.exceptions.DomainException;

public abstract class ShortenUrlValidationException extends DomainException {
    protected String code;
    protected String message;

    ShortenUrlValidationException(String code, String message) {
        super(code, message);
    }

    public String getMessage() {
        return this.message;
    }

    public String getCode() {
        return this.code;
    }

    public static class UrlFormatIsNotValid extends ShortenUrlValidationException {
        private final static String CODE = "URL_FORMAT_IS_NOT_VALID";

        public UrlFormatIsNotValid() {
            super(CODE, "The url format is not valid.");
        }
    }

    public static class UrlIsEmptyException extends ShortenUrlValidationException {
        private final static String CODE = "URL_IS_EMPTY";

        public UrlIsEmptyException() {
            super(CODE, "The url is empty.");
        }
    }

    public static class UrlIsNotHttpException extends ShortenUrlValidationException {
        private final static String CODE = "URL_IS_NOT_HTTP";

        public UrlIsNotHttpException(String url) {
            super(CODE, String.format("HTTP protocol is supported only. Provided url: %s", url));
        }
    }

    public static class UrlIsTooLongException extends ShortenUrlValidationException {
        private final static String CODE = "URL_IS_TOO_LONG";

        public UrlIsTooLongException(Integer expectedSize, Integer actualSize) {
            super(CODE, String.format("The url is too long. Expected Size: %s, Actual Size: %s", expectedSize, actualSize));
        }
    }

    public static class UrlIsMissingHostNameException extends ShortenUrlValidationException {
        private final static String CODE = "URL_IS_MISSING_A_HOSTNAME";

        public UrlIsMissingHostNameException(String url) {
            super(CODE, String.format("The provided url %s is missing hostname.", url));
        }
    }
}
