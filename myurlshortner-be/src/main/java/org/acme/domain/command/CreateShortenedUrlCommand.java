package org.acme.domain.command;


import java.util.Optional;

public record CreateShortenedUrlCommand(
        Optional<String> uniqueIdentifier,
        String originalUrl
) {
}
