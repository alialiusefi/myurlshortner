package org.acme.domain.entity;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Objects;

public class ShortenedUrl {
    private URI originalUrl;
    private final String publicIdentifier;
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private boolean isEnabled;

    public ShortenedUrl(URI originalUrl, String publicIdentifier) {
        this.originalUrl = originalUrl;
        this.publicIdentifier = publicIdentifier;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
        this.isEnabled = true;
    }

    public ShortenedUrl(String originalUrl, String publicIdentifier, OffsetDateTime datetime, OffsetDateTime updatedAt, Boolean isEnabled) {
        this.originalUrl = URI.create(originalUrl);
        this.publicIdentifier = publicIdentifier;
        this.createdAt = datetime;
        this.updatedAt = updatedAt;
        this.isEnabled = isEnabled;
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
        this.isEnabled = isEnabled;
        return this;
    }

    public boolean canRedirect() {
        return isEnabled;
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
        return isEnabled;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ShortenedUrl that = (ShortenedUrl) o;
        return isEnabled == that.isEnabled && Objects.equals(originalUrl, that.originalUrl) && Objects.equals(publicIdentifier, that.publicIdentifier) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalUrl, publicIdentifier, createdAt, updatedAt, isEnabled);
    }
}
