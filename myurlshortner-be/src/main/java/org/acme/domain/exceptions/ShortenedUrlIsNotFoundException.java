package org.acme.domain.exceptions;

public class ShortenedUrlIsNotFoundException extends DomainException {
    public ShortenedUrlIsNotFoundException() {
        super("SHORTENED_URL_NOT_FOUND", "The shortened url was not found.");
    }
}
