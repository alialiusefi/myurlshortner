package org.acme.application.repo.eventstore;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "shortened_url_event_store")
public class ShortenedUrlEventEntity {
    @Id
    UUID eventId;
    String uniqueIdentifier;
    @Embedded
    ShortenedUrlEventMetadata metadata;
    @JdbcTypeCode(SqlTypes.JSON)
    String event;

    public ShortenedUrlEventEntity() {
    }

    public ShortenedUrlEventEntity(UUID eventId, String uniqueIdentifier, ShortenedUrlEventMetadata metadata, String event) {
        this.eventId = eventId;
        this.uniqueIdentifier = uniqueIdentifier;
        this.metadata = metadata;
        this.event = event;
    }

    public ShortenedUrlEventMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(ShortenedUrlEventMetadata metadata) {
        this.metadata = metadata;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public void setUniqueIdentifier(String uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }
}

