package org.acme.domain.command;


public record CreateShortenedUrlCommand(
        String originalUrl
) {
}
