package org.acme.application.exception.giftrequest;

import org.acme.domain.exceptions.DomainException;
import org.acme.domain.exceptions.ShortenedUrlIsNotFoundException;

import java.util.List;
import java.util.Optional;

public record CreateGiftRequestError(
        Optional<ShortenedUrlIsNotFoundException> shortenedUrlNotFound,
        Optional<org.acme.domain.exceptions.giftrequest.CreateGiftRequestError> opError,
        List<DomainException> validationErrors
) {
}
