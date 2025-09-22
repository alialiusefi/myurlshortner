package org.acme.domain.entity;

import java.net.URI;
import java.time.OffsetDateTime;

public class ShortenedUrl {
    private URI originalUrl;
    private final String publicIdentifier;
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public ShortenedUrl(URI originalUrl, String publicIdentifier) {
        this.originalUrl = originalUrl;
        this.publicIdentifier = publicIdentifier;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public ShortenedUrl(String originalUrl, String publicIdentifier, OffsetDateTime datetime, OffsetDateTime updatedAt) {
        this.originalUrl = URI.create(originalUrl);
        this.publicIdentifier = publicIdentifier;
        this.createdAt = datetime;
        this.updatedAt = updatedAt;
    }

    public String shortenedUrl(String serviceHostname) {
        String format = "http://%s/goto/%s";
        return String.format(format, serviceHostname, this.publicIdentifier);
    }

    public ShortenedUrl updateOriginalUrl(URI newOriginalUrl) {
        this.originalUrl = newOriginalUrl;
        this.updatedAt = OffsetDateTime.now();
        return this;
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
}
