package org.acme.application.service.urlshortner;

import io.vavr.Tuple2;
import io.vavr.control.Either;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.acme.application.kafka.KafkaUrlPublisher;
import org.acme.application.repo.eventstore.ShortenedUrlEventRepository;
import org.acme.application.repo.urlshortner.ShortenedUrlCache;
import org.acme.domain.command.CreateShortenedUrlCommand;
import org.acme.domain.command.UpdateOriginalUrlCommand;
import org.acme.domain.entity.ShortenedUrl;
import org.acme.domain.entity.ShortenedUrlFactory;
import org.acme.domain.events.ShortenedUrlEvent;
import org.acme.domain.events.ShortenedUrlEventEnvelop;
import org.acme.domain.events.ShortenedUrlEventEnvelopFactory;
import org.acme.domain.events.V1UserUpdatedOriginalUrlEvent;
import org.acme.domain.exceptions.url.*;
import org.acme.domain.projection.AvailableShortenedUrl;
import org.acme.domain.repo.GiftRequestRepository;
import org.acme.domain.repo.SaveShortenedUrlConflictError;
import org.acme.domain.repo.ShortenedUrlRepository;
import org.acme.domain.service.ShortenedUrlService;
import org.acme.domain.validator.UniqueIdValidator;
import org.acme.domain.validator.UrlValidator;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.OffsetDateTime;
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
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    ShortenedUrlServiceImpl(
            ShortenedUrlRepository repo,
            ShortenedUrlEventRepository eventStore,
            KafkaUrlPublisher publisher,
            ShortenedUrlCache cache,
            GiftRequestRepository giftRequestRepo
    ) {
        this.repo = repo;
        this.eventStore = eventStore;
        this.publisher = publisher;
        this.cache = cache;
        this.giftRequestRepo = giftRequestRepo;
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

        String uniqueIdentifier = command.uniqueIdentifier().orElseGet(this::generateUniqueIdentifier);
        ShortenedUrl shortUrl = new ShortenedUrl(either.get(), uniqueIdentifier, command.userId());
        try {
            repo.insertShortenedUrl(shortUrl);
        } catch (SaveShortenedUrlConflictError err) {
            return Either.left(new ShortenUrlError(
                    Optional.of(new UniqueIdentifierAlreadyExists()),
                    List.of()
            ));
        }
        var event = ShortenedUrlEventEnvelopFactory.createV1CreatedShortenUrlEvent(shortUrl);
        eventStore.insertEvent(event);
        publisher.publishUserCreatedShortenedUrl(shortUrl.getCreatedAt(), shortUrl.getOriginalUrl(), shortUrl.getPublicIdentifier());
        cache.put(uniqueIdentifier, shortUrl);
        logger.debug("Successfully generated a short url!");
        return Either.right(shortUrl);
    }

    @Override
    public Tuple2<Long, List<AvailableShortenedUrl>> listOfAvailableUrls(@NonNull Integer page, @NonNull Integer size, boolean isAscending, @NonNull Long userId) {
        return repo.listAvailableShortenedUrls(page, size, isAscending, userId);
    }

    @Override
    @Transactional
    public Either<UpdateOriginalUrlError, ShortenedUrl> updateOriginalUrl(@NonNull UpdateOriginalUrlCommand command) {
        var maybeShortenedUrl = this.getShortenedUrl(command.uniqueIdentifier(), command.userId());
        if (maybeShortenedUrl.isEmpty()) {
            return Either.left(UpdateOriginalUrlError.createFromOperationError(new UpdateOriginalUrlException.ShortenedUrlIsNotFound()));
        }
        Either<List<UrlValidationException>, URI> urlEither = UrlValidator.validateUrl(hostname, command.newOriginalUrl());
        if (urlEither.isLeft()) {
            return Either.left(UpdateOriginalUrlError.createFromValidationErrors(urlEither.getLeft()));
        }

        return Either.right(maybeShortenedUrl.map(url -> {
            OffsetDateTime existingVersion = url.getUpdatedAt();
            boolean originalUrlHasChanged = !url.getOriginalUrl().equals(urlEither.get());
            url.updateOriginalUrl(urlEither.get(), command.isEnabled());
            repo.updateShortenedUrl(url, existingVersion);
            if (originalUrlHasChanged) {
                ShortenedUrlEventEnvelop<V1UserUpdatedOriginalUrlEvent> event = ShortenedUrlEventEnvelopFactory.createV1UpdatedOriginalUrlEvent(url);
                eventStore.insertEvent(event);
            }
            cache.put(command.uniqueIdentifier(), url);
            return url;
        }).get());
    }

    @Override
    public List<? extends ShortenedUrlEvent> getShortenedUrlHistory(
            @NonNull String uniqueIdentifier,
            @NonNull Integer offset,
            @NonNull Integer size,
            @NonNull OffsetDateTime from,
            @NonNull Long userId
    ) {
        return eventStore.getShortenedUrlEventsOrderedByDateTimeDesc(uniqueIdentifier, offset, size, from);
    }
}
