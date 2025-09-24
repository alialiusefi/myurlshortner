package org.acme.domain.exceptions.url;

import java.util.List;
import java.util.Optional;

public record GetUrlError(
        Optional<List<GetUrlValidationException>> urlValidationErrors,
        Optional<GetUrlException> operationError
) {
    public static GetUrlError createFromOperationErrors(GetUrlException operationError) {
        return new GetUrlError(Optional.empty(), Optional.of(operationError));
    }

    public static GetUrlError createFromValidationExceptions(List<GetUrlValidationException> validationExceptions) {
        return new GetUrlError(Optional.of(validationExceptions), Optional.empty());
    }
}
