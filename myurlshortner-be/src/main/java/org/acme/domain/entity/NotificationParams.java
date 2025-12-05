package org.acme.domain.entity;

public interface NotificationParams {
    public class ShortenedUrlReachedNViewsParams implements NotificationParams {
        String uniqueIdentifier;
        Integer views;

        public String getUniqueIdentifier() {
            return uniqueIdentifier;
        }

        public void setUniqueIdentifier(String uniqueIdentifier) {
            this.uniqueIdentifier = uniqueIdentifier;
        }

        public Integer getViews() {
            return views;
        }

        public void setViews(Integer views) {
            this.views = views;
        }
    }
}
