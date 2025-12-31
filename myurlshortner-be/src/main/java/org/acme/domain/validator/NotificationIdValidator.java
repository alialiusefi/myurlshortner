package org.acme.domain.validator;

import io.vavr.control.Either;
import org.acme.domain.exceptions.DomainException;
import org.acme.domain.exceptions.NotificationIdIsNotCorrectException;
import org.jspecify.annotations.Nullable;

public class NotificationIdValidator {
    public static Either<? extends DomainException, Long> validate(
            @Nullable String notificationId
    ) {
        if (notificationId == null) {
            return Either.left(new NotificationIdIsNotCorrectException(notificationId));
        }
        if (notificationId.isBlank()) {
            return Either.left(new NotificationIdIsNotCorrectException(notificationId));
        }
        try {
            var id = Long.parseLong(notificationId);
            if (id < 1) {
                return Either.left(new NotificationIdIsNotCorrectException(notificationId));
            } else {
                return Either.right(id);
            }
        } catch (NumberFormatException e) {
            return Either.left(new NotificationIdIsNotCorrectException(notificationId));
        }
    }
}
