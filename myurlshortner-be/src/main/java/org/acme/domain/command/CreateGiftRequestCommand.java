package org.acme.domain.command;

import org.acme.domain.entity.ShortenedUrl;

public record CreateGiftRequestCommand(
        ShortenedUrl shortenedUrl,
        Long targetUserId
) {
}
