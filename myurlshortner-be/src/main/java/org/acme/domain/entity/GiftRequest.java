package org.acme.domain.entity;

import java.time.OffsetDateTime;

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
}
