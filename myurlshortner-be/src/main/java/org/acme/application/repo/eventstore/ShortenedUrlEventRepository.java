package org.acme.application.repo.eventstore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.acme.domain.events.ShortenedUrlEventEnvelop;
import org.acme.domain.events.V4UserCreatedShortenedUrlEvent;

import java.util.Optional;
import java.util.UUID;

@Singleton
public class ShortenedUrlEventRepository implements PanacheRepository<ShortenedUrlEventEntity> {
    private final ObjectMapper mapper;

    ShortenedUrlEventRepository(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public Optional<ShortenedUrlEventEnvelop<?>> getShortenedUrlEventByEventId(UUID eventId) {
        return find("eventId = ?1", eventId).firstResultOptional().map(this::toShortenedUrlEvent);
    }

    @Transactional
    public void insertEvent(ShortenedUrlEventEnvelop<?> envelop) {
        if (count("eventId = ?1", envelop.getMetadata().getEventId()) != 0) {
            throw new IllegalStateException("Event already exists!");
        }
        var embeddedMetadata = toEmbeddedMetadata(envelop.getMetadata());
        switch (envelop.getEvent()) {
            case V4UserCreatedShortenedUrlEvent createdEvent -> {
                try {
                    var jsonString = mapper.writeValueAsString(createdEvent);
                    persist(
                            new ShortenedUrlEventEntity(
                                    envelop.getMetadata().getEventId(),
                                    createdEvent.uniqueIdentifier(),
                                    embeddedMetadata,
                                    jsonString
                            )
                    );
                } catch (JsonProcessingException e) {
                    throw new IllegalStateException("Incorrect json provided", e);
                }
            }
        }
    }

//    public Iterator<List<ShortenedUrlEventEnvelop<?>>> iterator(int batchSize, int isAscending, String uniqueIdentifier) {
//        return null;
//    }

    private ShortenedUrlEventMetadata toEmbeddedMetadata(ShortenedUrlEventEnvelop.Metadata metadata) {
        return new ShortenedUrlEventMetadata(
                metadata.getArtifactId(),
                metadata.getVersion(),
                metadata.getRecordName(),
                metadata.getEventDateTime(),
                metadata.getPublishedAt(),
                metadata.getProcessedAt()
        );
    }

    private ShortenedUrlEventEnvelop.Metadata toEnvelopMetadata(
            UUID eventId,
            ShortenedUrlEventMetadata embedded
    ) {
        return new ShortenedUrlEventEnvelop.Metadata(
                eventId,
                embedded.getArtifactId(),
                embedded.getVersion(),
                embedded.getRecordName(),
                embedded.getEventDateTime(),
                embedded.getProcessedAt(),
                embedded.getPublishedAt()
        );
    }

    private ShortenedUrlEventEnvelop<?> toShortenedUrlEvent(ShortenedUrlEventEntity dbEntity) {
        var meta = toEnvelopMetadata(dbEntity.getEventId(), dbEntity.getMetadata());
        switch (dbEntity.getMetadata().recordName) {
            case USER_CREATED_SHORTENED_URL -> {
                try {
                    var deserialized = mapper.readValue(dbEntity.event, V4UserCreatedShortenedUrlEvent.class);
                    return new ShortenedUrlEventEnvelop<>(
                            meta,
                            deserialized
                    );
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            default -> throw new IllegalStateException("Unsupported event type!");
        }
    }
}
