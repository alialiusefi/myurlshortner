package org.acme.domain.repo;

public class SaveShortenedUrlError extends Exception {
    private final boolean isConflict;

    public SaveShortenedUrlError(boolean isConflict) {
        this.isConflict = isConflict;
    }

    public boolean isSuccess() {
        return !isConflict;
    }
}
