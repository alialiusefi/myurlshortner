package org.acme.application.controller.url;

public record UpdateOriginalUrlRequest(
        String url,
        Boolean isEnabled
) {
}
