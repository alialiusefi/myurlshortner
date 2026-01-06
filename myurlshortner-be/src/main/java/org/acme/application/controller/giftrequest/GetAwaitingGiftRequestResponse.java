package org.acme.application.controller.giftrequest;

import java.time.OffsetDateTime;

public record GetAwaitingGiftRequestResponse(
        Long id,
        OffsetDateTime updatedAt
) {
}
