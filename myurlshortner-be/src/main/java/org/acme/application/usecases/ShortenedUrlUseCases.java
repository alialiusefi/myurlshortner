package org.acme.application.usecases;

import io.vavr.Tuple2;
import io.vavr.control.Either;
import io.vavr.control.Option;
import jakarta.inject.Singleton;
import org.acme.application.controller.url.PatchShortenedUrlRequest;
import org.acme.application.controller.url.ShortenUrlRequest;
import org.acme.application.exception.*;
import org.acme.application.exception.url.GetAvailableUrlsError;
import org.acme.application.exception.url.GetShortenedUrlError;
import org.acme.application.exception.url.GetShortenedUrlHistoryError;
import org.acme.application.exception.url.PatchShortenedUrlError;
import org.acme.domain.command.CreateShortenedUrlCommand;
import org.acme.domain.command.PatchShortenedUrlCommand;
import org.acme.domain.entity.ShortenedUrl;
import org.acme.domain.events.ShortenedUrlEvent;
import org.acme.domain.exceptions.DomainException;
import org.acme.domain.exceptions.ShortenedUrlIsNotFoundException;
import org.acme.domain.exceptions.UniqueIdentifierCannotBeEmptyValidationException;
import org.acme.domain.exceptions.UniqueIdentifierIsTooLongValidationException;
import org.acme.domain.exceptions.url.ShortenUrlError;
import org.acme.domain.projection.AvailableShortenedUrl;
import org.acme.domain.query.GetAvailableShortenedUrlsQuery;
import org.acme.domain.service.ShortenedUrlService;
import org.acme.domain.validator.TitleValidator;
import org.acme.domain.validator.UniqueIdValidator;
import org.acme.domain.validator.UrlValidator;
import org.acme.domain.validator.UserIdValidator;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Singleton
public class ShortenedUrlUseCases {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String apiKey;
    private final ShortenedUrlService service;
    private final String hostname;

    public ShortenedUrlUseCases(
            @ConfigProperty(name = "app.apikey") String apiKey,
            ShortenedUrlService service,
            @ConfigProperty(name = "app.hostname") String hostname
    ) {
        this.apiKey = apiKey;
        this.service = service;
        this.hostname = hostname;
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
                        userIdValidation.get(),
                        Optional.ofNullable(request.title())
                )
        );
    }

    public Either<List<DomainException>, List<String>> getShortenedUrlTitleSuggestions(String title, String userId) {
        var errors = new ArrayList<DomainException>();
        var validTitle = TitleValidator.validate(title).mapLeft(errors::add);
        var validUserId = UserIdValidator.validate(userId).mapLeft(errors::add);

        if (!errors.isEmpty()) {
            return Either.left(errors);
        }

        if (validTitle.get().isBlank()) {
            return Either.right(List.of());
        }

        return Either.right(
                service.getTitleSuggestions(
                        Arrays.stream(validTitle.get().split(" "))
                                .filter(a -> !a.isBlank())
                                .toList(),
                        validUserId.get()
                ));
    }

    public Either<GetAvailableUrlsError, Tuple2<Long, List<AvailableShortenedUrl>>> listAvailableUrls(
            String page,
            String size,
            String order,
            String title,
            String userId
    ) {
        List<ApplicationException> errors = new ArrayList<>();
        Integer validPage = null;
        Integer validSize = null;

        if (page == null) {
            errors.add(new PageNumberIsNotCorrectException(page));
        } else {
            try {
                validPage = Integer.parseInt(page);
                if (validPage < 1) {
                    errors.add(new PageNumberIsNotCorrectException(page));
                }
            } catch (NumberFormatException e) {
                errors.add(new PageNumberIsNotCorrectException(page));
            }
        }
        if (size == null) {
            errors.add(new PageSizeIsNotCorrectException(size));
        } else {
            try {
                validSize = Integer.parseInt(size);
                if (validSize < 1 || validSize > 100) {
                    errors.add(new PageSizeIsNotCorrectException(size));
                }
            } catch (NumberFormatException e) {
                errors.add(new PageSizeIsNotCorrectException(size));
            }
        }

        if (order != null) {
            var lowercase = order.toLowerCase();
            if (!lowercase.equals("desc") && !lowercase.equals("asc")) {
                errors.add(new OrderParamIsNotCorrectException(lowercase));
            }
        } else {
            order = "desc";
        }

        var validTitle = TitleValidator.validateNullable(title).mapLeft(
                a -> errors.add(new ApplicationException(a))
        );

        var userIdValidation = UserIdValidator.validate(userId);
        if (userIdValidation.isLeft()) {
            errors.add(new ApplicationException(userIdValidation.getLeft()));
        }

        if (errors.isEmpty()) {
            List<String> validTitleTokens = validTitle.get() == null ? null : Arrays.stream(validTitle.get().split(" "))
                    .filter(a -> !a.isBlank())
                    .toList();
            return Either.right(
                    service.listOfAvailableUrls(
                            new GetAvailableShortenedUrlsQuery(
                                    validPage,
                                    validSize,
                                    validTitleTokens,
                                    order.equals("asc"),
                                    userIdValidation.get()
                            )
                    )
            );
        } else {
            return Either.left(new GetAvailableUrlsError(errors));
        }
    }

    public Either<PatchShortenedUrlError, ShortenedUrl> patchShortenedUrl(
            PatchShortenedUrlRequest request,
            String userId,
            String uniqueIdentifier
    ) {
        var listOfErrors = new ArrayList<DomainException>();

        var validUserId = UserIdValidator.validate(userId).mapLeft(listOfErrors::add);
        UniqueIdValidator.validate(uniqueIdentifier).map(listOfErrors::add);
        if (request.title().isSet()) {
            TitleValidator.validate(request.title().value()).mapLeft(listOfErrors::add);
        }
        if (request.url().isSet()) {
            UrlValidator.validateUrl(hostname, request.url().value()).mapLeft(listOfErrors::addAll);
        }

        if (!listOfErrors.isEmpty()) {
            return Either.left(new PatchShortenedUrlError(Optional.empty(), listOfErrors));
        }

        var shortenedUrl = service.getShortenedUrl(
                uniqueIdentifier,
                validUserId.get()
        );
        if (shortenedUrl.isEmpty()) {
            return Either.left(
                    new PatchShortenedUrlError(Optional.of(new ShortenedUrlIsNotFoundException()), List.of())
            );
        }

        return Either.right(service.patchShortenedUrl(
                new PatchShortenedUrlCommand(
                        shortenedUrl.get(),
                        request.url().mapTo((a) -> UrlValidator.validateUrl(hostname, a).get()),
                        request.isEnabled(),
                        request.title(),
                        userId
                )
        ));
    }

    public Either<GetShortenedUrlHistoryError, List<? extends ShortenedUrlEvent>> getShortenedUrlHistory(
            String userId,
            String uniqueIdentifier,
            Integer offset,
            Integer size,
            String fromDateTime
    ) {
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
        var maybeShortenedUrl = service.getShortenedUrl(uniqueIdentifier, userIdValidation.get());
        if (maybeShortenedUrl.isEmpty()) {
            return Either.left(new GetShortenedUrlHistoryError(new ShortenedUrlIsNotFoundException()));
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

            return Option.ofOptional(service.getShortenedUrlInfo(uniqueIdentifier, userIdValidation.get())).toEither(
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
