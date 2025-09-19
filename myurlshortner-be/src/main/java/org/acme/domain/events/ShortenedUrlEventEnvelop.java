package org.acme.domain.events;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ShortenedUrlEventEnvelop<T extends ShortenedUrlEvent> {
    public static class Metadata {
        UUID eventId;
        String artifactId;
        Integer version;
        ShortenedUrlRecordType recordName;
        OffsetDateTime eventDateTime;
        OffsetDateTime processedAt;
        OffsetDateTime publishedAt;

        public Metadata(UUID eventId, String artifactId, Integer version, ShortenedUrlRecordType recordName, OffsetDateTime eventDateTime, OffsetDateTime processedAt, OffsetDateTime publishedAt) {
            this.eventId = eventId;
            this.artifactId = artifactId;
            this.version = version;
            this.recordName = recordName;
            this.eventDateTime = eventDateTime;
            this.processedAt = processedAt;
            this.publishedAt = publishedAt;
        }

        public UUID getEventId() {
            return eventId;
        }

        public void setEventId(UUID eventId) {
            this.eventId = eventId;
        }

        public String getArtifactId() {
            return artifactId;
        }

        public void setArtifactId(String artifactId) {
            this.artifactId = artifactId;
        }

        public Integer getVersion() {
            return version;
        }

        public void setVersion(Integer version) {
            this.version = version;
        }

        public ShortenedUrlRecordType getRecordName() {
            return recordName;
        }

        public void setRecordName(ShortenedUrlRecordType recordName) {
            this.recordName = recordName;
        }

        public OffsetDateTime getEventDateTime() {
            return eventDateTime;
        }

        public void setEventDateTime(OffsetDateTime eventDateTime) {
            this.eventDateTime = eventDateTime;
        }

        public OffsetDateTime getProcessedAt() {
            return processedAt;
        }

        public void setProcessedAt(OffsetDateTime processedAt) {
            this.processedAt = processedAt;
        }

        public OffsetDateTime getPublishedAt() {
            return publishedAt;
        }

        public void setPublishedAt(OffsetDateTime publishedAt) {
            this.publishedAt = publishedAt;
        }
    }

    private final Metadata metadata;
    private final T event;

    public ShortenedUrlEventEnvelop(Metadata metadata, T event) {
        this.metadata = metadata;
        this.event = event;
    }

    ShortenedUrlEventEnvelop(
            UUID eventId,
            String artifactId,
            Integer version,
            ShortenedUrlRecordType recordName,
            OffsetDateTime receivedAt,
            OffsetDateTime publishedAt,
            OffsetDateTime eventDateTime,
            T event
    ) {
        this.metadata = new Metadata(
                eventId,
                artifactId,
                version,
                recordName,
                eventDateTime,
                receivedAt,
                publishedAt
        );
        this.event = event;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public T getEvent() {
        return event;
    }
}
