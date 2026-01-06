package org.acme.domain.service;

import io.vavr.control.Option;
import org.acme.domain.command.CreateGiftRequestCommand;
import org.acme.domain.entity.GiftRequest;
import org.acme.domain.exceptions.giftrequest.CreateGiftRequestError;
import org.jspecify.annotations.NonNull;

public interface GiftRequestService {
    Option<CreateGiftRequestError> createGiftRequest(@NonNull CreateGiftRequestCommand command);

    Option<GiftRequest> getAwaitingGiftRequestByUniqueIdentifier(@NonNull String uniqueIdentifier, @NonNull Long userId);
}