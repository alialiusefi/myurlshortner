package org.acme.domain.query;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record GetAvailableShortenedUrlsQuery(
        @NonNull Integer page,
        @NonNull Integer size,
        @Nullable String title,
        boolean isAscending,
        @NonNull Long userId
) {
}
