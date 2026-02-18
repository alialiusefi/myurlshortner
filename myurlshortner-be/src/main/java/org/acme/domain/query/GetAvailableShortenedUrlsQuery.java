package org.acme.domain.query;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.List;

public record GetAvailableShortenedUrlsQuery(
        @NonNull Integer page,
        @NonNull Integer size,
        @Nullable List<String> title,
        boolean isAscending,
        @Nullable Long userId
) {
}
