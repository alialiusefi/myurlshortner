package org.acme.application.exception.notification;

import org.acme.domain.exceptions.DomainException;

import java.util.List;
import java.util.Optional;

public record ReadNotificationError(
        List<DomainException> validationException,
        Optional<DomainException> notFound
) {}
