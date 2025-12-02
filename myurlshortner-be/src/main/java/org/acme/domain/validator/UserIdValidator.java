package org.acme.domain.validator;

import io.vavr.control.Either;
import org.acme.application.exception.UserIdIsNotCorrectException;
import org.acme.domain.exceptions.DomainException;
import org.jspecify.annotations.Nullable;

public class UserIdValidator {
    public static Either<? extends DomainException, Long> validate(
            @Nullable String userId
    ) {
        if (userId == null) {
            return Either.left(new UserIdIsNotCorrectException(userId));
        }
        if (userId.isBlank()) {
            return Either.left(new UserIdIsNotCorrectException(userId));
        }
        try {
            var id = Long.parseLong(userId);
            if (id < 1) {
                return Either.left(new UserIdIsNotCorrectException(userId));
            } else {
                return Either.right(id);
            }
        } catch (NumberFormatException e) {
            return Either.left(new UserIdIsNotCorrectException(userId));
        }
    }
}
