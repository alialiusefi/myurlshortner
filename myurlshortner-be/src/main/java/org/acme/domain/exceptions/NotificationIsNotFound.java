package org.acme.domain.exceptions;

public class NotificationIsNotFound extends DomainException {
    public NotificationIsNotFound() {
        super("NOTIFICATION_IS_NOT_FOUND", "The notification was not found.");
    }
}
