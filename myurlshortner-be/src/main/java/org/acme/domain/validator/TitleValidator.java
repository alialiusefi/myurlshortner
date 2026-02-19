package org.acme.domain.validator;

import io.vavr.control.Either;
import org.acme.domain.exceptions.TitleIsNotCorrectException;

public class TitleValidator {
    public static Either<TitleIsNotCorrectException, String> validate(String titleInput) {
        if (titleInput == null || titleInput.length() > 100) {
            return Either.left(new TitleIsNotCorrectException());
        }
        return Either.right(titleInput);
    }

    public static Either<TitleIsNotCorrectException, String> validateNullable(String titleInput) {
        if (titleInput == null) {
            return Either.right(null);
        } else if (titleInput.length() > 100) {
            return Either.left(new TitleIsNotCorrectException());
        }
        return Either.right(titleInput);
    }
}
