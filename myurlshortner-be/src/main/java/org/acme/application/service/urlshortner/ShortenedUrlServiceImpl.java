package org.acme.application.service.urlshortner;

import io.vavr.Tuple2;
import io.vavr.control.Either;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.acme.application.kafka.KafkaUrlPublisher;
import org.acme.application.repo.eventstore.ShortenedUrlEventRepository;
import org.acme.application.repo.urlshortner.ShortenedUrlCache;
import org.acme.application.repo.urlshortner.ShortenedUrlReadRepositoryImpl;
import org.acme.domain.command.CreateShortenedUrlCommand;
import org.acme.domain.command.PatchShortenedUrlCommand;
import org.acme.domain.entity.ShortenedUrl;
import org.acme.domain.entity.ShortenedUrlFactory;
import org.acme.domain.events.ShortenedUrlEvent;
import org.acme.domain.events.ShortenedUrlEventEnvelop;
import org.acme.domain.events.ShortenedUrlEventEnvelopFactory;
import org.acme.domain.exceptions.url.ShortenUrlError;
import org.acme.domain.exceptions.url.UniqueIdentifierAlreadyExists;
import org.acme.domain.exceptions.url.UrlValidationException;
import org.acme.domain.projection.AvailableShortenedUrl;
import org.acme.domain.query.GetAvailableShortenedUrlsQuery;
import org.acme.domain.repo.GiftRequestRepository;
import org.acme.domain.repo.SaveShortenedUrlConflictError;
import org.acme.domain.repo.ShortenedUrlRepository;
import org.acme.domain.service.ShortenedUrlService;
import org.acme.domain.validator.TitleValidator;
import org.acme.domain.validator.UniqueIdValidator;
import org.acme.domain.validator.UrlValidator;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@Singleton
public class ShortenedUrlServiceImpl implements ShortenedUrlService {
    @ConfigProperty(name = "app.hostname")
    private String hostname;
    private final ShortenedUrlRepository repo;
    private final ShortenedUrlEventRepository eventStore;
    private final KafkaUrlPublisher publisher;
    private final ShortenedUrlCache cache;
    private final GiftRequestRepository giftRequestRepo;
    private final ShortenedUrlReadRepositoryImpl readRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    ShortenedUrlServiceImpl(
            ShortenedUrlRepository repo,
            ShortenedUrlEventRepository eventStore,
            KafkaUrlPublisher publisher,
            ShortenedUrlCache cache,
            GiftRequestRepository giftRequestRepo,
            ShortenedUrlReadRepositoryImpl readRepository
    ) {
        this.repo = repo;
        this.eventStore = eventStore;
        this.publisher = publisher;
        this.cache = cache;
        this.giftRequestRepo = giftRequestRepo;
        this.readRepository = readRepository;
    }

    @Override
    public @NonNull String generateUniqueIdentifier() {
        final int UNIQUE_IDENTIFIER_SIZE = 10;
        Random random = new Random();
        IntStream stream = random.ints(0, UniqueIdentifierCharTable.UNIQUE_ID_CHAR_TABLE.length);
        Character[] result = stream.mapToObj((gen) -> UniqueIdentifierCharTable.UNIQUE_ID_CHAR_TABLE[gen])
                .limit(UNIQUE_IDENTIFIER_SIZE)
                .toArray(Character[]::new);
        StringBuilder builder = new StringBuilder();
        for (Character i : result) {
            builder.append(i);
        }
        return builder.toString();
    }

    @Override
    public Optional<ShortenedUrl> getShortenedUrl(@NonNull String uniqueIdentifier, @Nullable Long userId) {
        return getShortenedUrlFromEvents(uniqueIdentifier, userId);
    }

    @Override
    public Optional<ShortenedUrl> getShortenedUrlInfo(@NonNull String uniqueIdentifier, @Nullable Long userId) {
        var optionalUrl = getShortenedUrlFromEvents(uniqueIdentifier, null);
        if (userId != null) {
            if (optionalUrl.isPresent()) {
                if (optionalUrl.get().getUserId().equals(userId)) {
                    return optionalUrl;
                } else {
                    var optionalGiftRequest = giftRequestRepo.getGiftRequestByUniqueIdentifierAndStatusIsAwaiting(
                            uniqueIdentifier,
                            null,
                            false
                    );
                    if (optionalGiftRequest.isPresent() && optionalGiftRequest.get().getTargetUserId().equals(userId)) {
                        return optionalUrl;
                    }
                    return Optional.empty();
                }
            }
        }
        return optionalUrl;
    }

    @Override
    public Optional<ShortenedUrl> getShortenedUrlFromEvents(@NonNull String uniqueIdentifier, @Nullable Long userId) {
        Supplier<Optional<ShortenedUrl>> fromDb = () -> {
            var maybeShortenedUrl = repo.getShortenedUrl(uniqueIdentifier, userId);
            if (maybeShortenedUrl.isEmpty()) {
                return maybeShortenedUrl;
            }
            var isEnabled = maybeShortenedUrl.get().isEnabled();
            return Optional.of(
                    ShortenedUrlFactory.createShortenedUrl(
                            eventStore.iteratorUntilLatest(10, uniqueIdentifier),
                            isEnabled
                    )
            );
        };
        var optionalUrl = cache.getByKey(uniqueIdentifier, fromDb);
        if (userId != null) {
            if (optionalUrl.isPresent()) {
                if (optionalUrl.get().getUserId().equals(userId)) {
                    return optionalUrl;
                } else {
                    return Optional.empty();
                }
            }
        }
        return optionalUrl;
    }

    @Override
    @Transactional
    public Either<ShortenUrlError, ShortenedUrl> createShortenedUrl(@NonNull CreateShortenedUrlCommand command) {
        Either<List<UrlValidationException>, URI> either = UrlValidator.validateUrl(hostname, command.originalUrl());
        if (either.isLeft()) {
            return Either.left(new ShortenUrlError(Optional.empty(), either.getLeft()));
        }
        if (command.uniqueIdentifier().isPresent()) {
            if (this.getShortenedUrl(command.uniqueIdentifier().get(), null).isPresent()) {
                return Either.left(new ShortenUrlError(
                        Optional.of(new UniqueIdentifierAlreadyExists()),
                        List.of()
                ));
            }

            var validationError = UniqueIdValidator.validate(command.uniqueIdentifier().get());
            if (validationError.isPresent()) {
                return Either.left(
                        new ShortenUrlError(Optional.empty(), List.of(validationError.get()))
                );
            }
        }
        if (command.title().isPresent()) {
            var titleValidation = TitleValidator.validate(command.title().get());
            if (titleValidation.isLeft()) {
                return Either.left(
                        new ShortenUrlError(Optional.empty(), List.of(titleValidation.getLeft()))
                );
            }
        }

        String uniqueIdentifier = command.uniqueIdentifier().orElseGet(this::generateUniqueIdentifier);
        ShortenedUrl shortUrl = new ShortenedUrl(
                either.get(),
                uniqueIdentifier,
                command.userId(),
                command.title().orElse(null)
        );
        try {
            repo.insertShortenedUrl(shortUrl);
        } catch (SaveShortenedUrlConflictError err) {
            return Either.left(new ShortenUrlError(
                    Optional.of(new UniqueIdentifierAlreadyExists()),
                    List.of()
            ));
        }
        var event = ShortenedUrlEventEnvelopFactory.createV2CreatedShortenUrlEvent(shortUrl);
        eventStore.insertEvent(event);
        publisher.publishUserCreatedShortenedUrl(
                shortUrl.getCreatedAt(),
                shortUrl.getOriginalUrl(),
                shortUrl.getPublicIdentifier(),
                shortUrl.getTitle(),
                shortUrl.getUserId()
        );
        cache.put(uniqueIdentifier, shortUrl);
        logger.debug("Successfully generated a short url!");
        return Either.right(shortUrl);
    }

    @Override
    public Tuple2<Long, List<AvailableShortenedUrl>> listOfAvailableUrls(GetAvailableShortenedUrlsQuery query) {
        if (query.title() == null) {
            return readRepository.getAvailableShortenedUrls(
                    query.page(),
                    query.size(),
                    query.isAscending(),
                    query.userId()
            );
        } else {
            return readRepository.getAvailableShortenedUrlsByTitle(
                    query.title().isBlank() ? List.of() : Arrays.asList(query.title().split(" ")),
                    query.userId(),
                    query.size(),
                    query.page()
            );
        }
    }

    @Transactional
    @Override
    public ShortenedUrl patchShortenedUrl(PatchShortenedUrlCommand command) {
        var existingVersion = command.shortenedUrl().getUpdatedAt();
        var current = command.shortenedUrl();
        if (command.isEnabled().isSet() && !current.isEnabled().equals(command.isEnabled().value())) {
            current.setIsEnabled(command.isEnabled().value());
            current.setUpdatedAt(OffsetDateTime.now());
        }
        if (command.url().isSet() && !current.getOriginalUrl().equals(command.url().value())) {
            var event = ShortenedUrlEventEnvelopFactory.createV1UpdatedOriginalUrlEvent(command.shortenedUrl(), command.url().value());
            current.updateOriginalUrl(event.getEvent());
            eventStore.insertEvent(event);
        }
        if (command.title().isSet() && (current.getTitle() == null || !current.getTitle().equals(command.title().value()))) {
            var event = ShortenedUrlEventEnvelopFactory.createV1UpdatedTitleEvent(command.shortenedUrl(), command.title().value());
            current.updateTitle(event.getEvent());
            eventStore.insertEvent(event);
        }
        if (!current.getUpdatedAt().equals(existingVersion)) {
            repo.updateShortenedUrl(current, existingVersion);
            cache.put(command.shortenedUrl().getPublicIdentifier(), current);
        }
        return current;
    }


    @Transactional
    public void giftShortenedUrl(String uid, Long targetUserId) {
        var shortenedUrl = this.getShortenedUrl(uid, null).get();
        var existingVersion = shortenedUrl.getUpdatedAt();
        var giftEvent = ShortenedUrlEventEnvelopFactory.createV2CreateUserGiftedShortenedUrlEvent(
                shortenedUrl,
                targetUserId
        );
        shortenedUrl.giftShortenedUrl(giftEvent.getEvent());
        eventStore.insertEvent(giftEvent);
        repo.updateShortenedUrl(shortenedUrl, existingVersion);
        cache.put(uid, shortenedUrl);
    }

    @Override
    public List<? extends ShortenedUrlEvent> getShortenedUrlHistory(
            @NonNull String uniqueIdentifier,
            @NonNull Integer offset,
            @NonNull Integer size,
            @NonNull OffsetDateTime from,
            @NonNull Long userId
    ) {
        var maybeLatestGiftedEventCreatedAt = eventStore.getLatestGiftedShortenedUrlEvent(uniqueIdentifier)
                .map(ShortenedUrlEventEnvelop::getMetadata)
                .map(ShortenedUrlEventEnvelop.Metadata::getEventDateTime)
                .getOrNull();
        return eventStore.getShortenedUrlEventsFromDateTimeToDateTimeOrderedByDateTimeDesc(uniqueIdentifier, offset, size, from, maybeLatestGiftedEventCreatedAt);
    }
}
