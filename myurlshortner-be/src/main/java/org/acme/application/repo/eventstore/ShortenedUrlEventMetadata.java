package org.acme.application.repo.eventstore;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.acme.domain.events.ShortenedUrlRecordType;

import java.time.OffsetDateTime;

@Embeddable
public class ShortenedUrlEventMetadata {
    private Integer version;

    @Enumerated(EnumType.STRING)
    private ShortenedUrlRecordType recordName;

    private OffsetDateTime eventDateTime;


    public ShortenedUrlEventMetadata() {
    }

    public ShortenedUrlEventMetadata(Integer version, ShortenedUrlRecordType recordName, OffsetDateTime eventDateTime) {
        this.version = version;
        this.recordName = recordName;
        this.eventDateTime = eventDateTime;
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
}
