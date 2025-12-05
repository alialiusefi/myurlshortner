package org.acme.domain.exceptions.notification;

import org.acme.domain.exceptions.DomainException;

public record GetLatestNotificationsError(
        DomainException validationError
) {
}
