package org.acme.application.repo.exception;

import java.time.OffsetDateTime;

public class ShortenedUrlOptimisticLockException extends RuntimeException {
    public ShortenedUrlOptimisticLockException(String uid, OffsetDateTime existingUpdatedAt) {
        super(String.format("ShortenedUrl with id '%s' with updatedAt '%s' has been updated/deleted already", uid, existingUpdatedAt));
    }
}
