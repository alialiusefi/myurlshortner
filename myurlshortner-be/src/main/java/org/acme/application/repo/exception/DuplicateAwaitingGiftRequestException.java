package org.acme.application.repo.exception;

public class DuplicateAwaitingGiftRequestException extends Exception {

    public DuplicateAwaitingGiftRequestException(String uniqueIdentifier) {
        super(String.format("Gift request for shortened url %S already exists.", uniqueIdentifier));
    }
}
