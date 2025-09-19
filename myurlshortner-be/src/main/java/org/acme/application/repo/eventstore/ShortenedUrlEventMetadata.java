package org.acme.application.repo.eventstore;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.acme.domain.events.ShortenedUrlRecordType;

import java.time.OffsetDateTime;

@Embeddable
public class ShortenedUrlEventMetadata {
    private String artifactId;
    private Integer version;

    @Enumerated(EnumType.STRING)
    private ShortenedUrlRecordType recordName;

    private OffsetDateTime eventDateTime;

    private OffsetDateTime publishedAt;

    private OffsetDateTime processedAt;

    public ShortenedUrlEventMetadata() {
    }

    public ShortenedUrlEventMetadata(String artifactId, Integer version, ShortenedUrlRecordType recordName, OffsetDateTime eventDateTime, OffsetDateTime publishedAt, OffsetDateTime processedAt) {
        this.artifactId = artifactId;
        this.version = version;
        this.recordName = recordName;
        this.eventDateTime = eventDateTime;
        this.publishedAt = publishedAt;
        this.processedAt = processedAt;
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

    public OffsetDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(OffsetDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public OffsetDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(OffsetDateTime processedAt) {
        this.processedAt = processedAt;
    }
}
