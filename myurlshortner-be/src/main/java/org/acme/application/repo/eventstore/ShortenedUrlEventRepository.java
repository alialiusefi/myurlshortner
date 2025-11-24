package org.acme.application.repo.eventstore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.acme.domain.events.*;
import org.jspecify.annotations.NonNull;

import java.time.OffsetDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ShortenedUrlEventRepository implements PanacheRepository<ShortenedUrlEventEntity> {
    private final ObjectMapper mapper;

    ShortenedUrlEventRepository(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Transactional
    public void cleanup() {
        deleteAll();
    }

    public Optional<ShortenedUrlEventEnvelop<?>> getShortenedUrlEventByEventId(UUID eventId) {
        return find("eventId = ?1", eventId).firstResultOptional().map(this::toShortenedUrlEvent);
    }

    public Optional<ShortenedUrlEventEnvelop<?>> getLatestShortenedUrlEventByIdAndType(String uniqueIdentifier, ShortenedUrlRecordType recordType) {
        return find("uniqueIdentifier = ?1 and metadata.recordName = ?2 order by metadata.eventDateTime desc limit 1", uniqueIdentifier, recordType).firstResultOptional().map(this::toShortenedUrlEvent);
    }

    @Transactional
    public void insertEvent(ShortenedUrlEventEnvelop<?> envelop) {
        if (count("eventId = ?1", envelop.getMetadata().getEventId()) != 0) {
            throw new IllegalStateException("Event already exists!");
        }
        var embeddedMetadata = toEmbeddedMetadata(envelop.getMetadata());
        try {
            switch (envelop.getEvent()) {
                case V1UserCreatedShortenedUrlEvent createdEvent -> {
                    var jsonString = mapper.writeValueAsString(createdEvent);
                    persist(
                            new ShortenedUrlEventEntity(
                                    envelop.getMetadata().getEventId(),
                                    createdEvent.uniqueIdentifier(),
                                    embeddedMetadata,
                                    jsonString
                            )
                    );

                }
                case V1UserUpdatedOriginalUrlEvent updatedEvent -> {
                    var jsonString = mapper.writeValueAsString(updatedEvent);
                    persist(
                            new ShortenedUrlEventEntity(
                                    envelop.getMetadata().getEventId(),
                                    updatedEvent.uniqueIdentifier(),
                                    embeddedMetadata,
                                    jsonString
                            )
                    );
                }
            }
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Incorrect json provided", e);
        }
    }

    public List<? extends ShortenedUrlEvent> getShortenedUrlEventsOrderedByDateTimeDesc(
            @NonNull String uniqueIdentifier,
            @NonNull Integer offset,
            @NonNull Integer size,
            @NonNull OffsetDateTime from
    ) {
        return find("uniqueIdentifier = ?1 and metadata.eventDateTime <= ?2 order by metadata.eventDateTime desc", uniqueIdentifier, from)
                .range(offset, (offset + size) - 1)
                .list()
                .stream()
                .map(this::toShortenedUrlEvent)
                .map(ShortenedUrlEventEnvelop::getEvent)
                .toList();
    }

    public Iterator<List<? extends ShortenedUrlEvent>> iteratorUntilLatest(int batchSize, String uniqueIdentifier) {
        return new ShortenedUrlEventIterator(this, uniqueIdentifier, batchSize);
    }

    private ShortenedUrlEventMetadata toEmbeddedMetadata(ShortenedUrlEventEnvelop.Metadata metadata) {
        return new ShortenedUrlEventMetadata(
                metadata.getVersion(),
                metadata.getRecordName(),
                metadata.getEventDateTime()
        );
    }

    private ShortenedUrlEventEnvelop.Metadata toEnvelopMetadata(
            UUID eventId,
            ShortenedUrlEventMetadata embedded
    ) {
        return new ShortenedUrlEventEnvelop.Metadata(
                eventId,
                embedded.getVersion(),
                embedded.getRecordName(),
                embedded.getEventDateTime()
        );
    }

    private ShortenedUrlEventEnvelop<? extends ShortenedUrlEvent> toShortenedUrlEvent(ShortenedUrlEventEntity dbEntity) {
        var meta = toEnvelopMetadata(dbEntity.getEventId(), dbEntity.getMetadata());
        try {
            switch (dbEntity.getMetadata().getRecordName()) {
                case USER_CREATED_SHORTENED_URL -> {
                    return new ShortenedUrlEventEnvelop<>(
                            meta,
                            mapper.readValue(dbEntity.getEvent(), V1UserCreatedShortenedUrlEvent.class)
                    );

                }
                case USER_UPDATED_ORIGINAL_URL -> {
                    return new ShortenedUrlEventEnvelop<>(
                            meta,
                            mapper.readValue(dbEntity.getEvent(), V1UserUpdatedOriginalUrlEvent.class)
                    );
                }
                default -> throw new IllegalStateException("Unsupported event type!");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static class ShortenedUrlEventIterator implements Iterator<List<? extends ShortenedUrlEvent>> {
        private final ShortenedUrlEventRepository repo;
        private int offset = 0;
        private final String query = "uniqueIdentifier = ?1 order by metadata.eventDateTime asc";
        private final String uniqueIdentifier;
        private final int batchSize;
        //private OffsetDateTime till;

        public ShortenedUrlEventIterator(ShortenedUrlEventRepository repo, String uniqueIdentifier, int batchSize) {
            this.repo = repo;
            this.uniqueIdentifier = uniqueIdentifier;
            this.batchSize = batchSize;
        }

        @Override
        public boolean hasNext() {
            var result = repo.find(query, uniqueIdentifier)
                    .range(offset, offset)
                    .list();
            return result.size() == 1;
        }

        @Override
        public List<? extends ShortenedUrlEvent> next() {
            var result = repo.find(query, uniqueIdentifier)
                    .range(offset, offset + batchSize - 1)
                    .list()
                    .stream().map(a -> repo.toShortenedUrlEvent(a).getEvent()).toList();
            this.offset = this.offset + batchSize;
            return result;
        }
    }

}
