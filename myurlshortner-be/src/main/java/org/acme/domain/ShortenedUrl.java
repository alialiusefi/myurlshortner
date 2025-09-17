package org.acme.domain;

import java.net.URI;
import java.time.OffsetDateTime;

public class ShortenedUrl {
    private final URI originalUrl;
    private final String publicIdentifier;
    private final OffsetDateTime createdAt;

    public ShortenedUrl(URI originalUrl, String publicIdentifier) {
        this.originalUrl = originalUrl;
        this.publicIdentifier = publicIdentifier;
        this.createdAt = OffsetDateTime.now();
    }

    public ShortenedUrl(String originalUrl, String publicIdentifier, OffsetDateTime datetime) {
        this.originalUrl = URI.create(originalUrl);
        this.publicIdentifier = publicIdentifier;
        this.createdAt = datetime;
    }

    public String shortenedUrl(String serviceHostname) {
        String format = "http://%s/goto/%s";
        return String.format(format, serviceHostname, this.publicIdentifier);
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
}
