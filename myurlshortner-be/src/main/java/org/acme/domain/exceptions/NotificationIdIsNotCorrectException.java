package org.acme.domain.exceptions;

public class NotificationIdIsNotCorrectException extends DomainException {
    public NotificationIdIsNotCorrectException(String id) {
        super("NOTIFICATION_ID_IS_NOT_CORRECT", String.format("The provided notification id %s is not correct. It must be a number above 0", id));
    }
}
