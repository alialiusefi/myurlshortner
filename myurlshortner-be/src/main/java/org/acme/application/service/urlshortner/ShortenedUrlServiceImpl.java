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
import org.acme.domain.events.ShortenedUrlEventEnvelop;
import org.acme.domain.events.ShortenedUrlEventEnvelopFactory;
import org.acme.domain.events.V5UserUpdatedOriginalUrl;
import org.acme.domain.exceptions.url.ShortenUrlError;
import org.acme.domain.exceptions.url.UpdateOriginalUrlError;
import org.acme.domain.exceptions.url.UpdateOriginalUrlException;
import org.acme.domain.exceptions.url.UrlValidationException;
import org.acme.domain.query.AvailableShortenedUrlWithAccessCount;
import org.acme.domain.repo.SaveShortenedUrlError;
import org.acme.domain.repo.ShortenedUrlRepository;
import org.acme.domain.service.ShortenedUrlService;
import org.acme.domain.service.UrlValidator;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
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

    private String generateUniqueIdentifier() {
        final int UNIQUE_IDENTIFIER_SIZE = 10;
        Random random = new Random();
        IntStream stream = random.ints(0, ASCIITable.VALID_ASCII_TABLE.length - 1);
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
    @Transactional
    public Either<ShortenUrlError, ShortenedUrl> generateShortenedUrl(@NonNull CreateShortenedUrlCommand command) throws SaveShortenedUrlError {
        Either<List<UrlValidationException>, URI> either = UrlValidator.validateUrl(hostname, command.originalUrl());
        if (either.isLeft()) {
            return Either.left(new ShortenUrlError(either.getLeft()));
        }

        String uniqueIdentifier = this.generateUniqueIdentifier();
        ShortenedUrl shortUrl = new ShortenedUrl(either.get(), uniqueIdentifier);
        repo.insertShortenedUrl(shortUrl);

        var event = ShortenedUrlEventEnvelopFactory.createV4CreatedShortenUrlEvent(
                shortUrl.getPublicIdentifier(),
                shortUrl.getCreatedAt(),
                shortUrl.getOriginalUrl()
        );
        eventStore.insertEvent(event);
        publisher.publishUserCreatedShortenedUrl(event.getEvent());
        logger.debug("Successfully generated a short url!");
        return Either.right(shortUrl);
    }

    @Override
    public Tuple2<Long, List<AvailableShortenedUrlWithAccessCount>> listOfAvailableUrls(@NonNull Integer page, @NonNull Integer size, boolean isAscending) {
        return repo.listAvailableShortenedUrls(page, size, isAscending);
    }

    @Override
    @Transactional
    public Either<UpdateOriginalUrlError, ShortenedUrl> updateOriginalUrl(@NonNull UpdateOriginalUrlCommand command) {
        var maybeShortenedUrl = repo.getShortenedUrl(command.uniqueIdentifier());
        if (maybeShortenedUrl.isEmpty()) {
            return Either.left(UpdateOriginalUrlError.createFromOperationError(Optional.of(new UpdateOriginalUrlException.ShortenedUrlIsNotFound())));
        }
        Either<List<UrlValidationException>, URI> either = UrlValidator.validateUrl(hostname, command.newOriginalUrl());
        if (either.isLeft()) {
            return Either.left(UpdateOriginalUrlError.createFromValidationErrors(either.getLeft()));
        }

        return Either.right(maybeShortenedUrl.map(
                url -> url.updateOriginalUrl(either.get())).map(
                repo::updateShortenedUrl
        ).map(
                shortenedUrl -> {
                    ShortenedUrlEventEnvelop<V5UserUpdatedOriginalUrl> event = ShortenedUrlEventEnvelopFactory.createV5UpdatedOriginalUrlEvent(
                            shortenedUrl.getPublicIdentifier(),
                            shortenedUrl.getOriginalUrl(),
                            shortenedUrl.getUpdatedAt()
                    );
                    eventStore.insertEvent(event);
                    publisher.publishUserUpdatedOriginalUrl(event.getEvent());
                    return shortenedUrl;
                }
        ).get());
    }
}
