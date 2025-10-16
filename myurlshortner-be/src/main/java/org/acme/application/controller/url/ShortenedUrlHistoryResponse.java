package org.acme.application.controller.url;

import java.time.OffsetDateTime;
import java.util.List;

public record ShortenedUrlHistoryResponse(
        List<ShortenedUrlHistoryRow> data
) {

    public record ShortenedUrlHistoryRow(
            String url,
            OffsetDateTime eventDateTime
    ) { }
}
