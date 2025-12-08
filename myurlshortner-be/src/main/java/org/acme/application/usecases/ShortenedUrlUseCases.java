package org.acme.application.usecases;

import io.vavr.Tuple2;
import io.vavr.control.Either;
import io.vavr.control.Option;
import jakarta.inject.Singleton;
import org.acme.application.controller.url.ShortenUrlRequest;
import org.acme.application.controller.url.UpdateOriginalUrlRequest;
import org.acme.application.exception.*;
import org.acme.application.exception.url.GetAvailableUrlsError;
import org.acme.application.exception.url.GetShortenedUrlError;
import org.acme.application.exception.url.GetShortenedUrlHistoryError;
import org.acme.domain.command.CreateShortenedUrlCommand;
import org.acme.domain.command.UpdateOriginalUrlCommand;
import org.acme.domain.entity.ShortenedUrl;
import org.acme.domain.events.ShortenedUrlEvent;
import org.acme.domain.exceptions.DomainException;
import org.acme.domain.exceptions.ShortenedUrlIsNotFoundException;
import org.acme.domain.exceptions.UniqueIdentifierCannotBeEmptyValidationException;
import org.acme.domain.exceptions.UniqueIdentifierIsTooLongValidationException;
import org.acme.domain.exceptions.url.ShortenUrlError;
import org.acme.domain.exceptions.url.UpdateOriginalUrlError;
import org.acme.domain.projection.AvailableShortenedUrl;
import org.acme.domain.service.ShortenedUrlService;
import org.acme.domain.validator.UserIdValidator;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class ShortenedUrlUseCases {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String apiKey;
    private final ShortenedUrlService service;

    public ShortenedUrlUseCases(@ConfigProperty(name = "app.apikey") String apiKey, ShortenedUrlService service) {
        this.apiKey = apiKey;
        this.service = service;
    }

    public Either<ShortenUrlError, ShortenedUrl> createShortenedUrl(String userId, ShortenUrlRequest request) {
        var userIdValidation = UserIdValidator.validate(userId);
        if (userIdValidation.isLeft()) {
            return Either.left(new ShortenUrlError(Optional.empty(), List.of(userIdValidation.getLeft())));
        }
        return service.createShortenedUrl(
                new CreateShortenedUrlCommand(
                        Optional.ofNullable(request.uniqueIdentifier()),
                        request.url(),
                        userIdValidation.get()
                )
        );
    }

    public Either<GetAvailableUrlsError, Tuple2<Long, List<AvailableShortenedUrl>>> listAvailableUrls(
            Integer page,
            Integer size,
            String order,
            String userId
    ) {
        List<ApplicationException> errors = new ArrayList<>();
        if (page == null || page < 1) {
            errors.add(new PageNumberIsNotCorrectException(page));
        }
        if (size == null || size < 1 || size > 100) {
            errors.add(new PageSizeIsNotCorrectException(size));
        }

        if (order != null) {
            var lowercase = order.toLowerCase();
            if (!lowercase.equals("desc") && !lowercase.equals("asc")) {
                errors.add(new OrderParamIsNotCorrectException(lowercase));
            }
        } else {
            order = "desc";
        }

        var userIdValidation = UserIdValidator.validate(userId);
        if (userIdValidation.isLeft()) {
            errors.add(new ApplicationException(userIdValidation.getLeft()));
        }

        if (errors.isEmpty()) {
            return Either.right(service.listOfAvailableUrls(page, size, order.equals("asc"), userIdValidation.get()));
        } else {
            return Either.left(new GetAvailableUrlsError(errors));
        }
    }

    public Either<UpdateOriginalUrlError, ShortenedUrl> updateOriginalUrl(
            String uniqueIdentifier,
            UpdateOriginalUrlRequest request,
            String userId
    ) {
        var userIdValidation = UserIdValidator.validate(userId);
        if (userIdValidation.isLeft()) {
            return Either.left(new UpdateOriginalUrlError(List.of(userIdValidation.getLeft()), Optional.empty()));
        }
        return service.updateOriginalUrl(
                new UpdateOriginalUrlCommand(uniqueIdentifier, request.url(), request.isEnabled(), userIdValidation.get())
        );
    }

    public Either<GetShortenedUrlHistoryError, List<? extends ShortenedUrlEvent>> getShortenedUrlHistory(
            String userId,
            String uniqueIdentifier,
            Integer offset,
            Integer size,
            String fromDateTime
    ) {
        var maybeShortenedUrl = service.getShortenedUrl(uniqueIdentifier, null);
        if (maybeShortenedUrl.isEmpty()) {
            return Either.left(new GetShortenedUrlHistoryError(new ShortenedUrlIsNotFoundException()));
        }
        List<ApplicationException> errors = new ArrayList<>();
        if (offset == null || offset < 0) {
            errors.add(new OffsetIsNotCorrectException(offset));
        }
        if (size == null || size <= 0 || size > 100) {
            errors.add(new PageSizeIsNotCorrectException(size));
        }
        OffsetDateTime parsed = null;
        try {
            parsed = OffsetDateTime.parse(fromDateTime.replace(" ", "+"));
        } catch (Throwable e) {
            errors.add(new DateTimeIsNotCorrectException(fromDateTime));
        }
        var userIdValidation = UserIdValidator.validate(userId);
        if (userIdValidation.isLeft()) {
            errors.add(new ApplicationException(userIdValidation.getLeft()));
        }

        if (errors.isEmpty()) {
            return Either.right(service.getShortenedUrlHistory(uniqueIdentifier, offset, size, parsed, userIdValidation.get()));
        } else {
            return Either.left(new GetShortenedUrlHistoryError(errors));
        }
    }

    public Either<GetShortenedUrlError, ShortenedUrl> getShortenedUrl(String userId, String uniqueIdentifier, String apiKey) {
        List<DomainException> errors = new ArrayList<>();
        if (uniqueIdentifier == null || uniqueIdentifier.isBlank()) {
            errors.add(new UniqueIdentifierCannotBeEmptyValidationException());
        } else if (uniqueIdentifier.length() > 10) {
            errors.add(new UniqueIdentifierIsTooLongValidationException());
        }

        if (userId != null && !userId.isBlank()) {
            var userIdValidation = UserIdValidator.validate(userId);
            if (userIdValidation.isLeft()) {
                errors.add(userIdValidation.getLeft());
            }

            if (!errors.isEmpty()) {
                return Either.left(new GetShortenedUrlError(errors));
            }

            return Option.ofOptional(service.getShortenedUrl(uniqueIdentifier, userIdValidation.get())).toEither(
                    new GetShortenedUrlError(new ShortenedUrlIsNotFoundException())
            );
        } else {
            if (!errors.isEmpty()) {
                return Either.left(new GetShortenedUrlError(errors));
            }
            if (apiKey == null || apiKey.isBlank()) {
                return Either.left(new GetShortenedUrlError(new ShortenedUrlIsNotFoundException()));
            } else if (!apiKey.equals(this.apiKey)) {
                return Either.left(new GetShortenedUrlError(new ShortenedUrlIsNotFoundException()));
            }
            return Option.ofOptional(service.getShortenedUrl(uniqueIdentifier, null)).toEither(
                    new GetShortenedUrlError(new ShortenedUrlIsNotFoundException())
            );
        }
    }

    public String generateUniqueIdentifier() {
        return service.generateUniqueIdentifier();
    }
}
