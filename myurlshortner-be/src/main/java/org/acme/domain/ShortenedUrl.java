package org.acme.domain;

import java.net.URI;

public class ShortenedUrl {
    private final URI originalUrl;
    private final String publicIdentifier;

    public ShortenedUrl(URI originalUrl, String publicIdentifier) {
        this.originalUrl = originalUrl;
        this.publicIdentifier = publicIdentifier;
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
}
