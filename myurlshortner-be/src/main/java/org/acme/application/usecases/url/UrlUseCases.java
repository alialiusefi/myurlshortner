package org.acme.application.usecases.url;

import io.vavr.Tuple2;
import io.vavr.control.Either;
import jakarta.inject.Singleton;
import org.acme.application.exception.ApplicationException;
import org.acme.application.exception.PageNumberIsNotCorrectException;
import org.acme.application.exception.PageSizeIsNotCorrectException;
import org.acme.application.exception.url.GetAvailableUrlsError;
import org.acme.domain.ShortenedUrl;
import org.acme.domain.exceptions.url.GetUrlError;
import org.acme.domain.exceptions.url.UrlValidationException;
import org.acme.domain.service.UrlService;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class UrlUseCases {
    final UrlService service;

    private UrlUseCases(UrlService service) {
        this.service = service;
    }

    public Either<GetUrlError, URI> getUrl(String uniqueIdentifier, String userAgent) {
        var errors = new ArrayList<UrlValidationException>();
        if (uniqueIdentifier == null || uniqueIdentifier.isBlank()) {
            errors.add(new UrlValidationException.UniqueIdentifierCannotBeEmptyValidationException());
        }
        if (userAgent == null || userAgent.isBlank()) {
            errors.add(new UrlValidationException.UserAgentCannotBeEmptyValidationException());
        }
        if (!errors.isEmpty()) {
            return Either.left(GetUrlError.createFromValidationExceptions(errors));
        }
        return service.getUrl(uniqueIdentifier, userAgent);
    }

    public Either<GetAvailableUrlsError, Tuple2<Long, List<ShortenedUrl>>> listAvailableUrls(Integer page, Integer size) {
        List<ApplicationException> errors = new ArrayList<>();
        if (page == null || page < 1) {
            errors.add(new PageNumberIsNotCorrectException(page));
        }
        if (size == null || size < 1 || size > 100) {
            errors.add(new PageSizeIsNotCorrectException(size));
        }
        if (errors.isEmpty()) {
            return Either.right(service.listOfAvailableUrls(page, size));
        } else {
            return Either.left(new GetAvailableUrlsError(errors));
        }
    }
}
