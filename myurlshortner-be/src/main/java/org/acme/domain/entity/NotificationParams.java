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
}
