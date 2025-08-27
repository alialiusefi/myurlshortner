package org.acme.domain.exceptions.url;

import java.util.List;
import java.util.Optional;

public record GetUrlError(
        Optional<List<UrlValidationException>> urlValidationErrors,
        Optional<List<GetUrlException>> operationErrors
) {
    public static GetUrlError createFromOperationErrors(List<GetUrlException> operationErrors) {
        return new GetUrlError(Optional.empty(), Optional.of(operationErrors));
    }

    public static GetUrlError createFromValidationExceptions(List<UrlValidationException> validationExceptions) {
        return new GetUrlError(Optional.of(validationExceptions), Optional.empty());
    }
}
