package org.acme.domain.exceptions;

public abstract class ShortenedUrlException extends Exception {
    protected String code;
    protected String message;

    public String getMessage() {
        return this.message;
    }

    public String getCode() {
        return this.code;
    }

    public static class UrlFormatIsNotValid extends ShortenedUrlException {
        private final static String CODE = "URL_FORMAT_IS_NOT_VALID";

        public UrlFormatIsNotValid() {
            this.message = "The url format is not valid.";
            this.code = CODE;
        }
    }

    public static class UrlIsEmptyException extends ShortenedUrlException {
        private final static String CODE = "URL_IS_EMPTY";

        public UrlIsEmptyException() {
            this.message = "The url is empty.";
            this.code = CODE;
        }
    }

    public static class UrlIsNotHttpException extends ShortenedUrlException {
        private final static String CODE = "URL_IS_NOT_HTTP";

        public UrlIsNotHttpException(String url) {
            this.code = CODE;
            this.message = String.format("HTTP protocol is supported only. Provided url: %s", url);
        }
    }

    public static class UrlIsTooLongException extends ShortenedUrlException {
        private final static String CODE = "URL_IS_TOO_LONG";

        public UrlIsTooLongException(Integer expectedSize, Integer actualSize) {
            this.message = String.format("The url is too long. Expected Size: %s, Actual Size: %s", expectedSize, actualSize);
            this.code = CODE;
        }
    }

    public static class UrlIsMissingHostNameException extends ShortenedUrlException {
        private final static String CODE = "URL_IS_MISSING_A_HOSTNAME";

        public UrlIsMissingHostNameException(String url) {
            this.message = String.format("The provided url %s is missing hostname.", url);
            this.code = CODE;
        }
    }
}
