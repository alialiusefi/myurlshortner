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

        public GiftRequestToTargetUserParams(String uniqueIdentifier, Long giftRequestId) {
            this.uniqueIdentifier = uniqueIdentifier;
            this.giftRequestId = giftRequestId;
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
}
