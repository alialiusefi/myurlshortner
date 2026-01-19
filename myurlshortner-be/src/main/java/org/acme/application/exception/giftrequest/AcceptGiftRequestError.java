package org.acme.application.exception.giftrequest;

import io.vavr.control.Option;
import org.acme.domain.exceptions.DomainException;
import org.acme.domain.exceptions.giftrequest.AwaitingGiftRequestWasNotFound;
import org.acme.domain.exceptions.giftrequest.GiftRequestWasUpdatedException;

import java.util.List;

public record AcceptGiftRequestError(
        Option<AwaitingGiftRequestWasNotFound> notFoundError,
        List<DomainException> validationErrors,
        Option<GiftRequestWasUpdatedException> wasUpdatedError
) {
}
