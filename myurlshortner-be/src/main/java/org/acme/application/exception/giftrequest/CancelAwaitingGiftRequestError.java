package org.acme.application.exception.giftrequest;

import org.acme.domain.exceptions.DomainException;
import org.acme.domain.exceptions.giftrequest.AwaitingGiftRequestWasNotFound;
import org.acme.domain.exceptions.giftrequest.GiftRequestWasUpdatedException;

import java.util.List;
import java.util.Optional;

public record CancelAwaitingGiftRequestError(
        Optional<AwaitingGiftRequestWasNotFound> notFound,
        Optional<GiftRequestWasUpdatedException> wasUpdatedError,
        List<DomainException> validationErrors
) {
}