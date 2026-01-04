package org.acme.domain.repo;

import org.acme.application.repo.exception.DuplicateAwaitingGiftRequestException;
import org.acme.domain.entity.GiftRequest;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public interface GiftRequestRepository {

    void saveGiftRequest(@NonNull GiftRequest giftRequest) throws DuplicateAwaitingGiftRequestException;

    Optional<GiftRequest> getGiftRequestByUniqueIdentifierAndStatusIsAwaiting(@NonNull String uniqueIdentifier);
}
