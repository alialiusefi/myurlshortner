package org.acme.domain.command;

import org.acme.application.util.PatchField;
import org.acme.domain.entity.ShortenedUrl;

import java.net.URI;

public record PatchShortenedUrlCommand(
        ShortenedUrl shortenedUrl,
        PatchField<URI> url,
        PatchField<Boolean> isEnabled,
        PatchField<String> title,
        String userId
) {
}
