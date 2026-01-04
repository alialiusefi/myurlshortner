package org.acme.domain.entity;

import java.time.OffsetDateTime;
import java.util.Objects;

public class GiftRequest {
    public enum GiftRequestStatus {
        AWAITING, ACCEPTED, DECLINED
    }

    private Long id;
    private Long sourceUserId;
    private Long targetUserId;
    private String publicIdentifier;
    private GiftRequestStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public GiftRequest(Long sourceUserId, Long targetUserId, String publicIdentifier,
                       GiftRequestStatus status, OffsetDateTime createdAt,
                       OffsetDateTime updatedAt
    ) {
        this.sourceUserId = sourceUserId;
        this.targetUserId = targetUserId;
        this.publicIdentifier = publicIdentifier;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getSourceUserId() {
        return sourceUserId;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public String getPublicIdentifier() {
        return publicIdentifier;
    }

    public GiftRequestStatus getStatus() {
        return status;
    }

    public void setStatus(GiftRequestStatus status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GiftRequest request = (GiftRequest) o;
        return Objects.equals(id, request.id) && Objects.equals(sourceUserId, request.sourceUserId) && Objects.equals(targetUserId, request.targetUserId) && Objects.equals(publicIdentifier, request.publicIdentifier) && status == request.status && Objects.equals(createdAt, request.createdAt) && Objects.equals(updatedAt, request.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sourceUserId, targetUserId, publicIdentifier, status, createdAt, updatedAt);
    }
}
