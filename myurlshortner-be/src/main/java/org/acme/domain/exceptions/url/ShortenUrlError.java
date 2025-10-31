package org.acme.domain.exceptions.url;

import org.acme.domain.exceptions.DomainException;

import java.util.List;
import java.util.Optional;

public record ShortenUrlError(
        Optional<UniqueIdentifierAlreadyExists> opError,
        List<? extends DomainException> validationExceptions
) {
}
