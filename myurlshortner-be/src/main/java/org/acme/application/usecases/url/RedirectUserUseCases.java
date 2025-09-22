package org.acme.application.usecases.url;

import io.vavr.control.Either;
import jakarta.inject.Singleton;
import org.acme.domain.exceptions.url.GetUrlError;
import org.acme.domain.exceptions.url.GetUrlValidationException;
import org.acme.domain.service.UrlService;

import java.net.URI;
import java.util.ArrayList;

@Singleton
public class RedirectUserUseCases {
    final UrlService service;

    private RedirectUserUseCases(UrlService service) {
        this.service = service;
    }

    public Either<GetUrlError, URI> getUrl(String uniqueIdentifier, String userAgent) {
        var errors = new ArrayList<GetUrlValidationException>();
        if (uniqueIdentifier == null || uniqueIdentifier.isBlank()) {
            errors.add(new GetUrlValidationException.UniqueIdentifierCannotBeEmptyValidationExceptionGet());
        }
        if (userAgent == null || userAgent.isBlank()) {
            errors.add(new GetUrlValidationException.UserAgentCannotBeEmptyValidationExceptionGet());
        }
        if (!errors.isEmpty()) {
            return Either.left(GetUrlError.createFromValidationExceptions(errors));
        }
        return service.getUrl(uniqueIdentifier, userAgent);
    }
}
