package org.acme.application.controller.giftrequest;

public record CreateGiftRequestRequest(
        String targetUserId
) {
}