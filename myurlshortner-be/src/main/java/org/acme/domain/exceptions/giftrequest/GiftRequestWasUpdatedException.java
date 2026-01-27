package org.acme.domain.exceptions.giftrequest;

import org.acme.domain.exceptions.DomainException;

public class GiftRequestWasUpdatedException extends DomainException {
    public GiftRequestWasUpdatedException() {
        super("GIFT_REQUEST_WAS_UPDATED", String.format("The gift request was already updated. Please retry."));
    }
}
