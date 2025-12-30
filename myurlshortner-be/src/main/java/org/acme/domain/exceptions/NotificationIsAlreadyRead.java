package org.acme.domain.exceptions;

public class NotificationIsAlreadyRead extends DomainException {
    public NotificationIsAlreadyRead() {
        super("NOTIFICATION_IS_ALREADY_READ", "The notification is already read.");
    }
}
