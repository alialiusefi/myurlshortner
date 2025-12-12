package org.acme.application.exception.notification;

import org.acme.domain.exceptions.DomainException;

public record GetLatestNotificationsError(
        DomainException validationError
) {
}
