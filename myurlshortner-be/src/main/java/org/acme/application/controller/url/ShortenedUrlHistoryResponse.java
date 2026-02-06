package org.acme.application.controller.url;

import org.acme.domain.events.ShortenedUrlRecordType;

import java.time.OffsetDateTime;
import java.util.List;

public record ShortenedUrlHistoryResponse(
        List<ShortenedUrlHistoryRow> data
) {
    public abstract static class ShortenedUrlHistoryRow {
        OffsetDateTime eventDateTime;
        ShortenedUrlRecordType type;

        public ShortenedUrlHistoryRow(OffsetDateTime datetime, ShortenedUrlRecordType type) {
            this.eventDateTime = datetime;
            this.type = type;
        }

        public OffsetDateTime getEventDateTime() {
            return eventDateTime;
        }

        public ShortenedUrlRecordType getType() {
            return type;
        }
    }

    public static class ShortenedUrlCreatedHistoryRow extends ShortenedUrlHistoryRow {
        String url;
        String title;

        public ShortenedUrlCreatedHistoryRow(OffsetDateTime datetime, String url, String title) {
            super(datetime, ShortenedUrlRecordType.USER_CREATED_SHORTENED_URL);
            this.url = url;
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public String getTitle() {
            return title;
        }
    }

    public static class UrlUpdatedHistoryRow extends ShortenedUrlHistoryRow {
        String url;

        UrlUpdatedHistoryRow(String url, OffsetDateTime datetime) {
            super(datetime, ShortenedUrlRecordType.USER_UPDATED_ORIGINAL_URL);
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }

    public static class TitleUpdatedHistoryRow extends ShortenedUrlHistoryRow {
        String title;

        TitleUpdatedHistoryRow(String title, OffsetDateTime datetime) {
            super(datetime, ShortenedUrlRecordType.USER_UPDATED_TITLE);
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }
}
