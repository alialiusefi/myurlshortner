package org.acme.domain.exceptions.giftrequest;

import org.acme.domain.exceptions.DomainException;

public sealed class CreateGiftRequestError extends DomainException {
    protected CreateGiftRequestError(String code, String message) {
        super(code, message);
    }

    public static final class GiftRequestTargetUserCannotBeTheSourceUser extends CreateGiftRequestError {
        public GiftRequestTargetUserCannotBeTheSourceUser(Long targetUserId) {
            super("TARGET_USER_CANNOT_BE_THE_SOURCE_USER", String.format("The provided target user id %s is the source target user id.", targetUserId));
        }
    }

    public static final class ShortenedUrlAlreadyHasAGiftRequest extends CreateGiftRequestError {
        public ShortenedUrlAlreadyHasAGiftRequest(String uniqueIdentifier) {
            super("SHORTENED_URL_ALREADY_HAS_A_GIFT_REQUEST", String.format("The shortened url with id %s has already a gift request pending.", uniqueIdentifier));
        }
    }

    public static final class TargetUserAlreadyHasSuchGiftRequest extends CreateGiftRequestError {
        public TargetUserAlreadyHasSuchGiftRequest(String uniqueIdentifier, Long userId) {
            super("TARGET_USER_ALREADY_HAS_SUCH_GIFT_REQUEST", String.format("The target user id %s has already a gift" +
                    " request pending for shortened url with id %s.", userId, uniqueIdentifier));
        }
    }

}
