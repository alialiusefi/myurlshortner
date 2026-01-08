package org.acme.domain.repo;

import io.vavr.control.Option;
import org.acme.application.repo.exception.DuplicateAwaitingGiftRequestException;
import org.acme.domain.entity.GiftRequest;
import org.acme.domain.exceptions.giftrequest.GiftRequestWasUpdatedException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface GiftRequestRepository {

    void saveGiftRequest(@NonNull GiftRequest giftRequest) throws DuplicateAwaitingGiftRequestException;

    Optional<GiftRequest> getGiftRequestByUniqueIdentifierAndStatusIsAwaiting(@NonNull String uniqueIdentifier, @Nullable Long sourceUserId);

    Optional<GiftRequest> getGiftRequestByIdAndStatus(@NonNull Long id, GiftRequest.@NonNull GiftRequestStatus status, @Nullable Long sourceUserId);

    Option<GiftRequestWasUpdatedException> updateGiftRequestStatusByIdAndUpdatedAt(@NonNull Long id, GiftRequest.@NonNull GiftRequestStatus status, @Nullable OffsetDateTime updatedAt);
}
