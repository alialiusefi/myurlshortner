package org.acme.application.repo.giftrequest;

import jakarta.persistence.*;
import org.acme.domain.entity.GiftRequest;

import java.time.OffsetDateTime;

@Entity
@Table(name = "gift_request")
public class GiftRequestEntity {
    @Id
    @GeneratedValue(generator = "gift_request_id_seq")
    private Long id;
    private String uniqueIdentifier;
    private Long sourceUserId;
    private Long targetUserId;
    @Enumerated(EnumType.STRING)
    private GiftRequest.GiftRequestStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public GiftRequestEntity() {
    }

    public GiftRequestEntity(Long id, String uniqueIdentifier, Long sourceUserId, Long targetUserId,
                             GiftRequest.GiftRequestStatus status,
                             OffsetDateTime createdAt,
                             OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.uniqueIdentifier = uniqueIdentifier;
        this.sourceUserId = sourceUserId;
        this.targetUserId = targetUserId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public void setUniqueIdentifier(String uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public Long getSourceUserId() {
        return sourceUserId;
    }

    public void setSourceUserId(Long sourceUserId) {
        this.sourceUserId = sourceUserId;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public GiftRequest.GiftRequestStatus getStatus() {
        return status;
    }

    public void setStatus(GiftRequest.GiftRequestStatus status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
