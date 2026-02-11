package org.acme.application.repo.eventstore;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "shortened_url_events")
public class ShortenedUrlEventEntity {
    @Id
    private UUID eventId;
    @Embedded
    private ShortenedUrlEventMetadata metadata;
    @JdbcTypeCode(SqlTypes.JSON)
    private String event;

    public ShortenedUrlEventEntity() {
    }

    public ShortenedUrlEventEntity(UUID eventId, ShortenedUrlEventMetadata metadata, String event) {
        this.eventId = eventId;
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
}
