package org.acme.domain.exceptions;

public class GiftRequestIdIsNotCorrectException extends DomainException {
    public GiftRequestIdIsNotCorrectException(String id) {
        super("GIFT_REQUEST_ID_IS_NOT_CORRECT", String.format("The provided gift request id %s is not correct. It must be a number above 0", id));
    }
}
