package org.acme.domain.exceptions.giftrequest;

public record CancelAwaitingGiftRequestError(
        GiftRequestWasUpdatedException wasUpdatedError
) {
}
