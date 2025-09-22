package org.acme.domain.command;

import org.jspecify.annotations.NonNull;

public record UpdateOriginalUrlCommand(
        @NonNull String uniqueIdentifier,
        @NonNull String newOriginalUrl
) {
}
