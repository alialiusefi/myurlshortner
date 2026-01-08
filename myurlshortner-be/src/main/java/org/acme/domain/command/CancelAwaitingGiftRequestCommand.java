package org.acme.domain.command;

import org.acme.domain.entity.GiftRequest;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.OffsetDateTime;

public record CancelAwaitingGiftRequestCommand(
        @NonNull GiftRequest giftRequest,
        @NonNull Long userId,
        @Nullable OffsetDateTime updatedAt
) {
}