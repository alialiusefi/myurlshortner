package org.acme.application.repo.giftrequest;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.acme.application.repo.exception.DuplicateAwaitingGiftRequestException;
import org.acme.domain.entity.GiftRequest;
import org.acme.domain.repo.GiftRequestRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

@ApplicationScoped
public class GiftRequestRepositoryImpl implements GiftRequestRepository, PanacheRepository<GiftRequestEntity> {

    @Transactional
    public void saveGiftRequest(@NonNull GiftRequest giftRequest) throws DuplicateAwaitingGiftRequestException {
        try {
            persist(new GiftRequestEntity(
                    giftRequest.getId(),
                    giftRequest.getPublicIdentifier(),
                    giftRequest.getSourceUserId(),
                    giftRequest.getTargetUserId(),
                    giftRequest.getStatus(),
                    giftRequest.getCreatedAt(),
                    giftRequest.getUpdatedAt()
            ));
        } catch (ConstraintViolationException e) {
            throw new DuplicateAwaitingGiftRequestException(giftRequest.getPublicIdentifier());
        }
    }

    public Optional<GiftRequest> getGiftRequestByUniqueIdentifierAndStatusIsAwaiting(@NonNull String uniqueIdentifier) {
        return find("uniqueIdentifier = ?1 and status = ?2",
                uniqueIdentifier,
                GiftRequest.GiftRequestStatus.AWAITING
        ).firstResultOptional().map(this::toGiftRequest);
    }

    public GiftRequest toGiftRequest(GiftRequestEntity entity) {
        GiftRequest request = new GiftRequest(
                entity.getSourceUserId(),
                entity.getTargetUserId(),
                entity.getUniqueIdentifier(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
        request.setId(entity.getId());
        return request;
    }

}
