package org.acme.domain.exceptions.url;

import org.acme.domain.exceptions.DomainException;

public class UpdateOriginalUrlException extends DomainException {

    protected UpdateOriginalUrlException(String code, String message) {
        super(code, message);
    }

    public static class ShortenedUrlIsNotFound extends UpdateOriginalUrlException {
        public ShortenedUrlIsNotFound() {
            super("SHORTENED_URL_NOT_FOUND", "The shortened url was not found.");
        }
    }
}
