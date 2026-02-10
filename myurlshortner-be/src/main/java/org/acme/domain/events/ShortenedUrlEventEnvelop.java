package org.acme.domain.events;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ShortenedUrlEventEnvelop<T extends ShortenedUrlEvent> {
    public static class Metadata {
        UUID eventId;
        Integer version;
        ShortenedUrlRecordType recordName;
        OffsetDateTime eventDateTime;
        String uniqueIdentifier;

        public Metadata(
                UUID eventId,
                Integer version,
                ShortenedUrlRecordType recordName,
                OffsetDateTime eventDateTime,
                String uniqueIdentifier
        ) {
            this.eventId = eventId;
            this.version = version;
            this.recordName = recordName;
            this.eventDateTime = eventDateTime;
            this.uniqueIdentifier = uniqueIdentifier;
        }

        public UUID getEventId() {
            return eventId;
        }

        public void setEventId(UUID eventId) {
            this.eventId = eventId;
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

        public String getUniqueIdentifier() {
            return uniqueIdentifier;
        }

        public void setUniqueIdentifier(String uniqueIdentifier) {
            this.uniqueIdentifier = uniqueIdentifier;
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
            Integer version,
            String uniqueIdentifier,
            ShortenedUrlRecordType recordName,
            OffsetDateTime eventDateTime,
            T event
    ) {
        this.metadata = new Metadata(
                eventId,
                version,
                recordName,
                eventDateTime,
                uniqueIdentifier
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
