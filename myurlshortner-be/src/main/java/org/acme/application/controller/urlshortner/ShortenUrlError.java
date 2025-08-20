package org.acme.application.controller.urlshortner;

public record ShortenUrlError(
        String code,
        String message
) {
}
