package org.acme.application.service.url;

import io.vavr.control.Either;
import jakarta.inject.Singleton;
import org.acme.application.kafka.KafkaUrlPublisher;
import org.acme.domain.exceptions.url.GetUrlError;
import org.acme.domain.exceptions.url.GetUrlException;
import org.acme.domain.exceptions.url.UrlValidationException;
import org.acme.domain.repo.ShortenedUrlRepository;
import org.acme.domain.service.UrlService;
import org.jspecify.annotations.NonNull;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
        var errors = new ArrayList<UrlValidationException>();
        if (uniqueIdentifier.isBlank()) {
            errors.add(new UrlValidationException.UniqueIdentifierCannotBeEmptyValidationException());
        }
        if (uniqueIdentifier.length() > 10) {
            errors.add(new UrlValidationException.UniqueIdentifierIsTooLongValidationException());
        }
        if (userAgent.isBlank()) {
            errors.add(new UrlValidationException.UserAgentCannotBeEmptyValidationException());
        }
        if (!errors.isEmpty()) {
            return Either.left(GetUrlError.createFromValidationExceptions(errors));
        }

        var shortenedUrl = repo.getShortenedUrl(uniqueIdentifier);
        if (shortenedUrl != null) {
            publisher.publishUserAccessedShortenedUrl(shortenedUrl, userAgent);
            return Either.right(shortenedUrl.getOriginalUrl());
        } else {
            return Either.left(GetUrlError.createFromOperationErrors(new GetUrlException.ShortenedUrlIsNotFound()));
        }
    }
}
