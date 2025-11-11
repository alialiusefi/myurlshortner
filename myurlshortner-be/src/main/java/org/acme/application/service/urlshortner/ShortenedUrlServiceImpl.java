package org.acme.application.service.urlshortner;

import io.vavr.Tuple2;
import io.vavr.control.Either;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.acme.application.kafka.KafkaUrlPublisher;
import org.acme.application.repo.eventstore.ShortenedUrlEventRepository;
import org.acme.domain.command.CreateShortenedUrlCommand;
import org.acme.domain.command.UpdateOriginalUrlCommand;
import org.acme.domain.entity.ShortenedUrl;
import org.acme.domain.entity.ShortenedUrlFactory;
import org.acme.domain.events.ShortenedUrlEvent;
import org.acme.domain.events.ShortenedUrlEventEnvelop;
import org.acme.domain.events.ShortenedUrlEventEnvelopFactory;
import org.acme.domain.events.V1UserUpdatedOriginalUrlEvent;
import org.acme.domain.exceptions.UniqueIdentifierCannotBeEmptyValidationException;
import org.acme.domain.exceptions.UniqueIdentifierIsTooLongValidationException;
import org.acme.domain.exceptions.url.*;
import org.acme.domain.projection.AvailableShortenedUrl;
import org.acme.domain.repo.SaveShortenedUrlConflictError;
import org.acme.domain.repo.ShortenedUrlRepository;
import org.acme.domain.service.ShortenedUrlService;
import org.acme.domain.service.UrlValidator;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

@Singleton
public class ShortenedUrlServiceImpl implements ShortenedUrlService {
    @ConfigProperty(name = "app.hostname")
    private String hostname;
    private final ShortenedUrlRepository repo;
    private final ShortenedUrlEventRepository eventStore;
    private final KafkaUrlPublisher publisher;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    ShortenedUrlServiceImpl(ShortenedUrlRepository repo, ShortenedUrlEventRepository eventStore, KafkaUrlPublisher publisher) {
        this.repo = repo;
        this.eventStore = eventStore;
        this.publisher = publisher;
    }

    @Override
    public @NonNull String generateUniqueIdentifier() {
        final int UNIQUE_IDENTIFIER_SIZE = 10;
        Random random = new Random();
        IntStream stream = random.ints(0, ASCIITable.VALID_ASCII_TABLE.length);
        Character[] result = stream.mapToObj((gen) -> ASCIITable.VALID_ASCII_TABLE[gen])
                .limit(UNIQUE_IDENTIFIER_SIZE)
                .toArray(Character[]::new);
        StringBuilder builder = new StringBuilder();
        for (Character i : result) {
            builder.append(i);
        }
        return builder.toString();
    }

    @Override
    public Optional<ShortenedUrl> getShortenedUrl(@NonNull String uniqueIdentifier) {
        return getShortenedUrlFromEvents(uniqueIdentifier);
    }

    @Override
    public Optional<ShortenedUrl> getShortenedUrlFromEvents(@NonNull String uniqueIdentifier) {
        var maybeShortenedUrl = repo.getShortenedUrl(uniqueIdentifier);
        if (maybeShortenedUrl.isEmpty()) {
            return maybeShortenedUrl;
        }
        var isEnabled = maybeShortenedUrl.get().isEnabled();
        return Optional.of(
                ShortenedUrlFactory.createShortenedUrl(
                        eventStore.iteratorFromStart(10, uniqueIdentifier),
                        isEnabled
                )
        );
    }

    @Override
    @Transactional
    public Either<ShortenUrlError, ShortenedUrl> createShortenedUrl(@NonNull CreateShortenedUrlCommand command) {
        Either<List<UrlValidationException>, URI> either = UrlValidator.validateUrl(hostname, command.originalUrl());
        if (either.isLeft()) {
            return Either.left(new ShortenUrlError(Optional.empty(), either.getLeft()));
        }
        if (command.uniqueIdentifier().isPresent()) {
            if (repo.getShortenedUrl(command.uniqueIdentifier().get()).isPresent()) {
                return Either.left(new ShortenUrlError(
                        Optional.of(new UniqueIdentifierAlreadyExists()),
                        List.of()
                ));
            }
            // todo: extract unique identifier validation into separate domain validator
            if (command.uniqueIdentifier().get().isBlank()) {
                return Either.left(
                        new ShortenUrlError(
                                Optional.empty(),
                                List.of(new UniqueIdentifierCannotBeEmptyValidationException())
                        )
                );
            }

            if (command.uniqueIdentifier().get().length() > 10) {
                return Either.left(
                        new ShortenUrlError(
                                Optional.empty(),
                                List.of(new UniqueIdentifierIsTooLongValidationException())
                        )
                );
            }
        }

        String uniqueIdentifier = command.uniqueIdentifier().orElseGet(this::generateUniqueIdentifier);
        ShortenedUrl shortUrl = new ShortenedUrl(either.get(), uniqueIdentifier);
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
        logger.debug("Successfully generated a short url!");
        return Either.right(shortUrl);
    }

    @Override
    public Tuple2<Long, List<AvailableShortenedUrl>> listOfAvailableUrls(@NonNull Integer page, @NonNull Integer size, boolean isAscending) {
        return repo.listAvailableShortenedUrls(page, size, isAscending);
    }

    @Override
    @Transactional
    public Either<UpdateOriginalUrlError, ShortenedUrl> updateOriginalUrl(@NonNull UpdateOriginalUrlCommand command) {
        var maybeShortenedUrl = repo.getShortenedUrl(command.uniqueIdentifier());
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
            return url;
        }).get());
    }

    @Override
    public List<? extends ShortenedUrlEvent> getShortenedUrlHistory(
            @NonNull String uniqueIdentifier,
            @NonNull Integer offset,
            @NonNull Integer size,
            @NonNull OffsetDateTime from
    ) {
        return eventStore.getShortenedUrlEventsOrderedByDateTimeDesc(uniqueIdentifier, offset, size, from);
    }
}
