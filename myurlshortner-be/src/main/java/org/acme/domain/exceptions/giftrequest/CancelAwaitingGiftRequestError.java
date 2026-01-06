package org.acme.domain.exceptions.giftrequest;

public record CancelAwaitingGiftRequestError(
        AwaitingGiftRequestWasNotFound notFound,
        GiftRequestWasUpdatedException wasUpdatedError
) {
}
