package org.acme.domain.entity;

public interface NotificationParams {
    class ShortenedUrlReachedNViewsParams implements NotificationParams {
        String uniqueIdentifier;
        Long views;

        public ShortenedUrlReachedNViewsParams(String uniqueIdentifier, Long views) {
            this.uniqueIdentifier = uniqueIdentifier;
            this.views = views;
        }

        public String getUniqueIdentifier() {
            return uniqueIdentifier;
        }

        public void setUniqueIdentifier(String uniqueIdentifier) {
            this.uniqueIdentifier = uniqueIdentifier;
        }

        public Long getViews() {
            return views;
        }

        public void setViews(Long views) {
            this.views = views;
        }
    }

    class GiftRequestToTargetUserParams implements NotificationParams {
        String uniqueIdentifier;
        Long giftRequestId;
        Long sourceUserId;

        public GiftRequestToTargetUserParams(String uniqueIdentifier, Long giftRequestId, Long sourceUserId) {
            this.uniqueIdentifier = uniqueIdentifier;
            this.giftRequestId = giftRequestId;
            this.sourceUserId = sourceUserId;
        }

        public Long getSourceUserId() {
            return sourceUserId;
        }

        public void setSourceUserId(Long sourceUserId) {
            this.sourceUserId = sourceUserId;
        }

        public String getUniqueIdentifier() {
            return uniqueIdentifier;
        }

        public void setUniqueIdentifier(String uniqueIdentifier) {
            this.uniqueIdentifier = uniqueIdentifier;
        }

        public Long getGiftRequestId() {
            return giftRequestId;
        }

        public void setGiftRequestId(Long giftRequestId) {
            this.giftRequestId = giftRequestId;
        }
    }

    class GiftRequestResponseToSourceUserParams implements NotificationParams {
        public enum GiftRequestResponseToSourceUserType {
            ACCEPTED,
            DECLINED,
            EXPIRED
        }

        Long giftRequestId;
        String uniqueIdentifier;
        Long targetUserId;
        GiftRequestResponseToSourceUserType type;

        public GiftRequestResponseToSourceUserParams(
                Long giftRequestId,
                Long targetUserId,
                GiftRequestResponseToSourceUserType type,
                String uniqueIdentifier
        ) {
            this.giftRequestId = giftRequestId;
            this.targetUserId = targetUserId;
            this.type = type;
            this.uniqueIdentifier = uniqueIdentifier;
        }

        public Long getGiftRequestId() {
            return giftRequestId;
        }

        public void setGiftRequestId(Long giftRequestId) {
            this.giftRequestId = giftRequestId;
        }

        public Long getTargetUserId() {
            return targetUserId;
        }

        public void setTargetUserId(Long targetUserId) {
            this.targetUserId = targetUserId;
        }

        public GiftRequestResponseToSourceUserType getType() {
            return type;
        }

        public void setType(GiftRequestResponseToSourceUserType type) {
            this.type = type;
        }

        public String getUniqueIdentifier() {
            return uniqueIdentifier;
        }

        public void setUniqueIdentifier(String uniqueIdentifier) {
            this.uniqueIdentifier = uniqueIdentifier;
        }
    }
}
