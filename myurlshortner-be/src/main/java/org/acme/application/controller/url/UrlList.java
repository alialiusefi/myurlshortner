package org.acme.application.controller.url;

import java.util.List;

public record UrlList(
        List<UrlRow> data,
        Long total
) {
    public record UrlRow(
            String url,
            String shortenedUrl
    ) {
    }
}
