package org.acme.domain.exceptions.url;

import java.util.List;
import java.util.Optional;

public record UpdateOriginalUrlError(
        List<UrlValidationException> validationErrors,
        Optional<UpdateOriginalUrlException> operationError
) {
    public static UpdateOriginalUrlError createFromValidationErrors(List<UrlValidationException> errors) {
        return new UpdateOriginalUrlError(errors, Optional.empty());
    }

    public static UpdateOriginalUrlError createFromOperationError(Optional<UpdateOriginalUrlException> error) {
        return new UpdateOriginalUrlError(List.of(), error);
    }
}
