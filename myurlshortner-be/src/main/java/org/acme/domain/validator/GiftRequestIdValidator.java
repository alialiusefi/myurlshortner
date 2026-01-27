package org.acme.domain.validator;

import io.vavr.control.Either;
import org.acme.domain.exceptions.DomainException;
import org.acme.domain.exceptions.GiftRequestIdIsNotCorrectException;
import org.jspecify.annotations.Nullable;

public class GiftRequestIdValidator {
    public static Either<? extends DomainException, Long> validate(
            @Nullable String giftRequestId
    ) {
        if (giftRequestId == null) {
            return Either.left(new GiftRequestIdIsNotCorrectException(giftRequestId));
        }
        if (giftRequestId.isBlank()) {
            return Either.left(new GiftRequestIdIsNotCorrectException(giftRequestId));
        }
        try {
            var id = Long.parseLong(giftRequestId);
            if (id < 1) {
                return Either.left(new GiftRequestIdIsNotCorrectException(giftRequestId));
            } else {
                return Either.right(id);
            }
        } catch (NumberFormatException e) {
            return Either.left(new GiftRequestIdIsNotCorrectException(giftRequestId));
        }
    }
}
