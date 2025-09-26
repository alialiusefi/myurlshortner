package org.acme.application.service.url;

import io.vavr.control.Either;
import jakarta.inject.Singleton;
import org.acme.application.kafka.KafkaUrlPublisher;
import org.acme.domain.exceptions.url.GetUrlError;
import org.acme.domain.exceptions.url.GetUrlException;
import org.acme.domain.exceptions.url.GetUrlValidationException;
import org.acme.domain.repo.ShortenedUrlRepository;
import org.acme.domain.service.UrlService;
import org.jspecify.annotations.NonNull;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;

@Singleton
public class UrlServiceImpl implements UrlService {
    final ShortenedUrlRepository repo;
    final KafkaUrlPublisher publisher;

    private UrlServiceImpl(ShortenedUrlRepository repo, KafkaUrlPublisher publisher) {
        this.repo = repo;
        this.publisher = publisher;
    }

    public Either<GetUrlError, URI> getUrl(
            @NonNull String uniqueIdentifier,
            @NonNull String userAgent
    ) {
        var errors = new ArrayList<GetUrlValidationException>();
        if (uniqueIdentifier.isBlank()) {
            errors.add(new GetUrlValidationException.UniqueIdentifierCannotBeEmptyValidationException());
        }
        if (uniqueIdentifier.length() > 10) {
            errors.add(new GetUrlValidationException.UniqueIdentifierIsTooLongValidationException());
        }
        if (userAgent.isBlank()) {
            errors.add(new GetUrlValidationException.UserAgentCannotBeEmptyValidationException());
        }
        if (!errors.isEmpty()) {
            return Either.left(GetUrlError.createFromValidationExceptions(errors));
        }

        var maybeShortenedUrl = repo.getShortenedUrl(uniqueIdentifier);
        var accessedAt = OffsetDateTime.now();

        if (maybeShortenedUrl.isEmpty()) {
            return Either.left(GetUrlError.createFromOperationErrors(new GetUrlException.ShortenedUrlIsNotFound()));
        } else {
            var shortenedUrl = maybeShortenedUrl.get();
            if (shortenedUrl.canRedirect()) {
                publisher.publishUserAccessedShortenedUrl(shortenedUrl, userAgent, accessedAt);
                return Either.right(shortenedUrl.getOriginalUrl());
            } else {
                return Either.left(GetUrlError.createFromOperationErrors(new GetUrlException.ShortenedUrlIsNotFound()));
            }
        }
    }
}
