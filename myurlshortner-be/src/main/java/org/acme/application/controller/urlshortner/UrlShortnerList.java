package org.acme.application.controller.urlshortner;

import java.util.List;

public record UrlShortnerList(
        List<UrlShortnerListRow> data,
        Long total
) {
    record UrlShortnerListRow(
            String url,
            String shortenedUrl
    ) {
    }
}
