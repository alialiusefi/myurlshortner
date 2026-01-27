package org.acme.application.exception.giftrequest;

import org.acme.domain.exceptions.DomainException;
import org.acme.domain.exceptions.giftrequest.AwaitingGiftRequestWasNotFound;

import java.util.List;
import java.util.Optional;

public record GetAwaitingGiftRequestError(
        Optional<AwaitingGiftRequestWasNotFound> notFound,
        List<DomainException> validationError
) {
}
