package org.acme.domain.exceptions.url;

import org.acme.domain.exceptions.DomainException;

import java.util.List;
import java.util.Optional;

public record UpdateOriginalUrlError(
        List<? extends DomainException> validationErrors,
        Optional<UpdateOriginalUrlException> operationError
) {
    public static UpdateOriginalUrlError createFromValidationErrors(List<? extends DomainException> errors) {
        return new UpdateOriginalUrlError(errors, Optional.empty());
    }

    public static UpdateOriginalUrlError createFromOperationError(UpdateOriginalUrlException error) {
        return new UpdateOriginalUrlError(List.of(), Optional.of(error));
    }
}
