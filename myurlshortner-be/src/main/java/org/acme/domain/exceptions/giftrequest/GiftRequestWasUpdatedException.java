package org.acme.domain.exceptions.giftrequest;

import org.acme.domain.exceptions.DomainException;

public class GiftRequestWasUpdatedException extends DomainException {
    protected GiftRequestWasUpdatedException(Long id) {
        super("GIFT_REQUEST_WAS_UPDATED", String.format("The provided gift request id %s is not correct. It must be a number above 0", id));
    }
}
