package org.acme.domain.exceptions;

public class TitleIsNotCorrectException extends DomainException {
    public TitleIsNotCorrectException() {
        super("TITLE_IS_NOT_CORRECT",
                "The title provided is not correct. It cannot exceed 100 characters or be null.");
    }
}
