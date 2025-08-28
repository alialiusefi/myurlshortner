package org.acme.domain.exceptions.url;

import org.acme.domain.exceptions.DomainException;

public abstract class GetUrlException extends DomainException {
    GetUrlException(String message, String code) {
        super(message, code);
    }

    public static class ShortenedUrlIsNotFound extends GetUrlException {
        public ShortenedUrlIsNotFound() {
            super("SHORTENED_URL_NOT_FOUND", "The shortened url was not found.");
        }
    }
}
