package org.acme.domain.command;

import org.acme.domain.entity.GiftRequest;
import org.jspecify.annotations.NonNull;

import java.time.OffsetDateTime;

public record CancelAwaitingGiftRequestCommand(
        @NonNull GiftRequest giftRequest,
        @NonNull Long userId,
        @NonNull OffsetDateTime updatedAt
) { }