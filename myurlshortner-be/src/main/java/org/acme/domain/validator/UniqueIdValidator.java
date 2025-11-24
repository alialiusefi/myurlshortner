package org.acme.domain.validator;

import org.acme.application.service.urlshortner.UniqueIdentifierCharTable;
import org.acme.domain.exceptions.DomainException;
import org.acme.domain.exceptions.UniqueIdContainsInvalidCharactersValidationException;
import org.acme.domain.exceptions.UniqueIdentifierCannotBeEmptyValidationException;
import org.acme.domain.exceptions.UniqueIdentifierIsTooLongValidationException;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class UniqueIdValidator {

    public static Optional<? extends DomainException> validate(@NonNull String uniqueIdentifier) {
        if (uniqueIdentifier.isBlank()) {
            return Optional.of(new UniqueIdentifierCannotBeEmptyValidationException());
        }

        if (uniqueIdentifier.length() > 10) {
            return Optional.of(new UniqueIdentifierIsTooLongValidationException());
        }

        var containsBadCharacters = uniqueIdentifier
                .chars()
                .anyMatch(q -> !UniqueIdentifierCharTable.UNIQUE_ID_CHAR_SET.contains((char) q));
        if (containsBadCharacters) {
            return Optional.of(new UniqueIdContainsInvalidCharactersValidationException());
        }
        return Optional.empty();
    }
}
