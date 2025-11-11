package org.acme.domain.validator;

import org.acme.domain.exceptions.DomainException;
import org.acme.domain.exceptions.UniqueIdentifierCannotBeEmptyValidationException;
import org.acme.domain.exceptions.UniqueIdContainsInvalidCharactersValidationException;
import org.acme.domain.exceptions.UniqueIdentifierIsTooLongValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

class UniqueIdValidatorTest {

    @Test
    void validate_shouldReturnEmptyOptional_whenIdIsValid() {
        String validId = "abc-123-Z";
        Optional<? extends DomainException> result = UniqueIdValidator.validate(validId);

        assert result.isEmpty();
    }

    @Test
    void validate_shouldReturnEmptyOptional_whenIdIsAtMaxLength() {
        String validId = "1234567890";
        Optional<? extends DomainException> result = UniqueIdValidator.validate(validId);

        assert result.isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
    void validate_shouldReturnEmptyError_whenIdIsBlank(String blankId) {
        Optional<? extends DomainException> result = UniqueIdValidator.validate(blankId);

        assert result.isPresent();
        assert result.get() instanceof UniqueIdentifierCannotBeEmptyValidationException;
    }

    @Test
    void validate_shouldReturnTooLongError_whenIdIsOver10Chars() {
        String longId = "abc-123-Z-1";
        Optional<? extends DomainException> result = UniqueIdValidator.validate(longId);

        assert result.isPresent();
        assert result.get() instanceof UniqueIdentifierIsTooLongValidationException;
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc*123", "id!", "bad$char", "with space"})
    void validate_shouldReturnInvalidCharError_whenIdContainsBadChars(String invalidId) {
        Optional<? extends DomainException> result = UniqueIdValidator.validate(invalidId);

        assert result.isPresent();
        assert result.get() instanceof UniqueIdContainsInvalidCharactersValidationException;
    }
}
