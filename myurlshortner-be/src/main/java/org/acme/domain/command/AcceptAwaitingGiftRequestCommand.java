package org.acme.domain.command;

import java.time.OffsetDateTime;

public record AcceptAwaitingGiftRequestCommand(
        Long giftRequestId,
        OffsetDateTime updatedAt
) {
}
