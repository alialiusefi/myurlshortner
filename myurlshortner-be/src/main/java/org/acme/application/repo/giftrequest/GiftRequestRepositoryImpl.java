package org.acme.application.repo.giftrequest;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.vavr.control.Option;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.acme.application.repo.exception.DuplicateAwaitingGiftRequestException;
import org.acme.domain.entity.GiftRequest;
import org.acme.domain.exceptions.giftrequest.GiftRequestWasUpdatedException;
import org.acme.domain.repo.GiftRequestRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class GiftRequestRepositoryImpl implements GiftRequestRepository, PanacheRepository<GiftRequestEntity> {

    @Transactional
    public Long saveGiftRequest(@NonNull GiftRequest giftRequest) throws DuplicateAwaitingGiftRequestException {
        try {
            var entity = new GiftRequestEntity(
                    giftRequest.getId(),
                    giftRequest.getPublicIdentifier(),
                    giftRequest.getSourceUserId(),
                    giftRequest.getTargetUserId(),
                    giftRequest.getStatus(),
                    giftRequest.getCreatedAt(),
                    giftRequest.getUpdatedAt()
            );
            persist(entity);
            return entity.getId();
        } catch (ConstraintViolationException e) {
            throw new DuplicateAwaitingGiftRequestException(giftRequest.getPublicIdentifier());
        }
    }

    public Optional<GiftRequest> getGiftRequestByUniqueIdentifierAndStatusIsAwaiting(
            @NonNull String uniqueIdentifier,
            @Nullable Long sourceUserId,
            boolean readLock
    ) {
        PanacheQuery<GiftRequestEntity> query;
        if (sourceUserId == null) {
            query = find("uniqueIdentifier = ?1 and status = ?2",
                    uniqueIdentifier,
                    GiftRequest.GiftRequestStatus.AWAITING
            );
        } else {
            query = find("uniqueIdentifier = ?1 and status = ?2 and sourceUserId = ?3",
                    uniqueIdentifier,
                    GiftRequest.GiftRequestStatus.AWAITING,
                    sourceUserId
            );
        }
        if (readLock) {
            query.withLock(LockModeType.PESSIMISTIC_READ);
        }
        return query.firstResultOptional().map(this::toGiftRequest);
    }

    @Override
    public Optional<GiftRequest> getGiftRequestByIdAndStatus(@NonNull Long id, GiftRequest.@NonNull GiftRequestStatus status, @Nullable Long sourceUserId) {
        if (sourceUserId == null) {
            return find("id = ?1 and status = ?2",
                    id,
                    status
            ).firstResultOptional().map(this::toGiftRequest);
        } else {
            return find("id = ?1 and sourceUserId = ?2 and status = ?3",
                    id,
                    sourceUserId,
                    status
            ).firstResultOptional().map(this::toGiftRequest);
        }
    }

    public List<GiftRequest> findAwaitingGiftRequestWhereCreatedAtIsLessThanHoursFromDateTime(
            @NonNull Integer size,
            @NonNull Integer hours,
            @NonNull OffsetDateTime datetime
    ) {
        var query = getEntityManager().createNativeQuery(
                """
                        select id, unique_identifier, source_user_id, target_user_id, status, created_at, updated_at
                        from gift_request
                        where status = ?1 and ?2 - created_at >= interval '24 hours'
                        limit ?3
                        """.stripIndent(),
                GiftRequestEntity.class
        );
        query.setParameter(1, GiftRequest.GiftRequestStatus.AWAITING.name());
        query.setParameter(2, OffsetDateTime.now());
        query.setParameter(3, size);
        List<GiftRequestEntity> results = query.getResultList();
        return results.stream().map(this::toGiftRequest).toList();
    }

    @Override
    @Transactional
    public Option<GiftRequestWasUpdatedException> updateGiftRequestStatusByIdAndUpdatedAt(@NonNull Long id,
                                                                                          GiftRequest.@NonNull GiftRequestStatus status,
                                                                                          @Nullable OffsetDateTime updatedAt) {
        var criteriaUpdate = getEntityManager().getCriteriaBuilder().createCriteriaUpdate(GiftRequestEntity.class);
        var root = criteriaUpdate.from(GiftRequestEntity.class);

        criteriaUpdate.set("status", status);
        criteriaUpdate.set("updatedAt", ZonedDateTime.now());
        var idPred = root.get("id").equalTo(id);
        var updatedAtPred = updatedAt == null ? root.get("updatedAt").isNull() : root.get("updatedAt").equalTo(updatedAt);
        criteriaUpdate.where(idPred, updatedAtPred);

        var query = getEntityManager().createQuery(criteriaUpdate);
        var res = query.executeUpdate();
        return res == 0 ? Option.of(new GiftRequestWasUpdatedException()) : Option.none();
    }

    @Override
    @Transactional
    public void cleanup() {
        deleteAll();
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
