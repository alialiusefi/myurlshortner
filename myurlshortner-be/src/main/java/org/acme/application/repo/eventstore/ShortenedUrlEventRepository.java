package org.acme.application.repo.eventstore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.vavr.control.Option;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.acme.domain.events.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.*;

import static org.acme.domain.events.ShortenedUrlRecordType.USER_GIFTED_SHORTENED_URL;

@ApplicationScoped
//todo rename to EventRepository
public class ShortenedUrlEventRepository implements PanacheRepository<ShortenedUrlEventEntity> {
    private final ObjectMapper mapper;

    ShortenedUrlEventRepository(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Transactional
    public void cleanup() {
        deleteAll();
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

                case V1UserGiftedShortenedUrlEvent giftEvent -> {
                    var jsonString = mapper.writeValueAsString(giftEvent);
                    persist(
                            new ShortenedUrlEventEntity(
                                    envelop.getMetadata().getEventId(),
                                    giftEvent.uniqueIdentifier(),
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

    public List<? extends ShortenedUrlEvent> getShortenedUrlEventsFromDateTimeToDateTimeOrderedByDateTimeDesc(
            @NonNull String uniqueIdentifier,
            @NonNull Integer offset,
            @NonNull Integer size,
            @NonNull OffsetDateTime from,
            @Nullable OffsetDateTime to
    ) {
        var builder = getEntityManager().getCriteriaBuilder();
        var criteriaQuery = builder.createQuery(ShortenedUrlEventEntity.class);
        var root = criteriaQuery.from(ShortenedUrlEventEntity.class);
        var predicates = new ArrayList<Predicate>() {{
            add(root.get("uniqueIdentifier").equalTo(uniqueIdentifier));
            add(builder.lessThanOrEqualTo(root.get("metadata").get("eventDateTime"), from));
            if (to != null) {
                add(builder.greaterThanOrEqualTo(root.get("metadata").get("eventDateTime"), to));
            }
        }};
        criteriaQuery = criteriaQuery.where(predicates).orderBy(
                builder.desc(root.get("metadata").get("eventDateTime"))
        );
        var query = getEntityManager()
                .createQuery(criteriaQuery)
                .setFirstResult(offset)
                .setMaxResults(size);
        return query.getResultStream().map(this::toShortenedUrlEvent).map(ShortenedUrlEventEnvelop::getEvent).toList();
    }

    public Option<ShortenedUrlEventEnvelop<?>> getLatestGiftedShortenedUrlEvent(
            String uid
    ) {
        return Option.ofOptional(
                find(
                        "uniqueIdentifier = ?1 and metadata.recordName = ?2 order by metadata.eventDateTime desc",
                        uid,
                        USER_GIFTED_SHORTENED_URL
                )
                        .page(Page.ofSize(1))
                        .firstResultOptional()
                        .map(this::toShortenedUrlEvent)
        );
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
                case USER_GIFTED_SHORTENED_URL -> {
                    return new ShortenedUrlEventEnvelop<>(
                            meta,
                            mapper.readValue(dbEntity.getEvent(), V1UserGiftedShortenedUrlEvent.class)
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
