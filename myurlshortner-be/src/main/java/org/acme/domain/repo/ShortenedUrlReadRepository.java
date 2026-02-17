package org.acme.domain.repo;

import io.vavr.Tuple2;
import org.acme.domain.projection.AvailableShortenedUrl;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ShortenedUrlReadRepository {

    Tuple2<Long, List<AvailableShortenedUrl>> getAvailableShortenedUrlsByTitle(
            @NonNull List<String> titleKeywords,
            @Nullable Long userId,
            @NonNull Integer size,
            @NonNull Integer page
    );

    Tuple2<Long, List<AvailableShortenedUrl>> getAvailableShortenedUrls(
            @NonNull Integer page,
            @NonNull Integer size,
            boolean isAscending,
            @Nullable Long userId
    );
}
