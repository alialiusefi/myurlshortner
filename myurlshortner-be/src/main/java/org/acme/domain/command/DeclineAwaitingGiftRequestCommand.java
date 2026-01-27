package org.acme.domain.command;

import org.acme.domain.entity.GiftRequest;

import java.time.OffsetDateTime;

public record DeclineAwaitingGiftRequestCommand(
        GiftRequest giftRequest,
        OffsetDateTime updatedAt
) {
}
