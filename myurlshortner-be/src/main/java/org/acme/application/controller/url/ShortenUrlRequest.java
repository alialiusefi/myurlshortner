package org.acme.application.controller.url;

public record ShortenUrlRequest(
        String uniqueIdentifier,
        String url
) {
}
