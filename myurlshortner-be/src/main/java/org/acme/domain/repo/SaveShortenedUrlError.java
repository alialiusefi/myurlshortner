package org.acme.domain.repo;

public class SaveShortenedUrlError extends Exception {
    public SaveShortenedUrlError(String uid) {
        super(String.format("Such shortened url with uid '%s' already exists!", uid));
    }
}
