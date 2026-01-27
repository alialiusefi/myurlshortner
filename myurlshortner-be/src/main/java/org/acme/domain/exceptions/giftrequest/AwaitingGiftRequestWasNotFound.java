package org.acme.domain.exceptions.giftrequest;

import org.acme.domain.exceptions.DomainException;

public class AwaitingGiftRequestWasNotFound extends DomainException {
    public AwaitingGiftRequestWasNotFound(String uniqueIdentifier) {
        super("AWAITING_GIFT_REQUEST_WAS_NOT_FOUND",
                String.format("Cannot find awaiting gift request for unique identifier %s.", uniqueIdentifier));
    }

    public AwaitingGiftRequestWasNotFound(Long id) {
        super("AWAITING_GIFT_REQUEST_WAS_NOT_FOUND",
                String.format("Cannot find awaiting gift request with id %s.", id));
    }
}
