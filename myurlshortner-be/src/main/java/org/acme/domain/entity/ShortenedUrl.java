package org.acme.domain.entity;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Objects;

public class ShortenedUrl {
    private URI originalUrl;
    private String publicIdentifier;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private boolean enabled;
    private Long userId;

    public ShortenedUrl() {
    }

    public ShortenedUrl(URI originalUrl, String publicIdentifier, Long userId) {
        this.originalUrl = originalUrl;
        this.publicIdentifier = publicIdentifier;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
        this.enabled = true;
        this.userId = userId;
    }

    public ShortenedUrl(String originalUrl, String publicIdentifier, OffsetDateTime datetime, OffsetDateTime updatedAt, Boolean enabled, Long userId) {
        this.originalUrl = URI.create(originalUrl);
        this.publicIdentifier = publicIdentifier;
        this.createdAt = datetime;
        this.updatedAt = updatedAt;
        this.enabled = enabled;
        this.userId = userId;
    }

    public String shortenedUrl(String serviceHostname) {
        return ShortenedUrl.toShortenedUrl(serviceHostname, this.publicIdentifier);
    }

    public static String toShortenedUrl(String serviceHostname, String publicIdentifier) {
        String format = "http://%s/goto/%s";
        return String.format(format, serviceHostname, publicIdentifier);
    }

    public ShortenedUrl updateOriginalUrl(URI newOriginalUrl, Boolean isEnabled) {
        this.originalUrl = newOriginalUrl;
        this.updatedAt = OffsetDateTime.now();
        this.enabled = isEnabled;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean canRedirect() {
        return enabled;
    }

    public URI getOriginalUrl() {
        return originalUrl;
    }

    public String getPublicIdentifier() {
        return publicIdentifier;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setOriginalUrl(URI originalUrl) {
        this.originalUrl = originalUrl;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.enabled = isEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ShortenedUrl that = (ShortenedUrl) o;
        return enabled == that.enabled && Objects.equals(originalUrl, that.originalUrl) && Objects.equals(publicIdentifier, that.publicIdentifier) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalUrl, publicIdentifier, createdAt, updatedAt, enabled, userId);
    }
}
