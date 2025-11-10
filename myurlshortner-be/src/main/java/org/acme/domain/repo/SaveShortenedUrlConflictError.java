package org.acme.domain.repo;

public class SaveShortenedUrlConflictError extends Exception {
    public SaveShortenedUrlConflictError(String uid) {
        super(String.format("Such shortened url with uid '%s' already exists!", uid));
    }
}
