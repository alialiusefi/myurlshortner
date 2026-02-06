package org.acme.application.controller.url;

import java.time.OffsetDateTime;
import java.util.List;

public record ShortenedUrlHistoryResponse(
        List<ShortenedUrlHistoryRow> data
) {
    public abstract static class ShortenedUrlHistoryRow {
        OffsetDateTime eventDateTime;

        public ShortenedUrlHistoryRow(OffsetDateTime datetime) {
            this.eventDateTime = datetime;
        }

        public ShortenedUrlHistoryRow() {
        }

        public OffsetDateTime getEventDateTime() {
            return eventDateTime;
        }

        public void setEventDateTime(OffsetDateTime eventDateTime) {
            this.eventDateTime = eventDateTime;
        }
    }

    public static class UrlUpdatedHistoryRow extends ShortenedUrlHistoryRow {
        String url;

        UrlUpdatedHistoryRow() {
            super();
        }

        UrlUpdatedHistoryRow(String url, OffsetDateTime datetime) {
            super(datetime);
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class TitleUpdatedHistoryRow extends ShortenedUrlHistoryRow {
        String title;

        TitleUpdatedHistoryRow(String title, OffsetDateTime datetime) {
            super(datetime);
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
