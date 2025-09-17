package org.acme.application.controller.url;

import java.time.OffsetDateTime;
import java.util.List;

public record UrlList(
        List<UrlRow> data,
        Long total
) {
    public record UrlRow(
            String url,
            String shortenedUrl,
            Long accessCount,
            OffsetDateTime createdAt
    ) {
    }
}
