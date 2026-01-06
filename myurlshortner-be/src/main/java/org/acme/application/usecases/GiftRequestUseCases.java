package org.acme.application.usecases;

import io.vavr.control.Either;
import io.vavr.control.Option;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.application.controller.giftrequest.CancelAwaitingGiftRequestRequest;
import org.acme.application.controller.giftrequest.CreateGiftRequestRequest;
import org.acme.application.exception.giftrequest.CancelAwaitingGiftRequestError;
import org.acme.application.exception.giftrequest.CreateGiftRequestError;
import org.acme.application.exception.giftrequest.GetAwaitingGiftRequestError;
import org.acme.domain.command.CancelAwaitingGiftRequestCommand;
import org.acme.domain.command.CreateGiftRequestCommand;
import org.acme.domain.entity.GiftRequest;
import org.acme.domain.exceptions.DomainException;
import org.acme.domain.exceptions.ShortenedUrlIsNotFoundException;
import org.acme.domain.exceptions.UpdatedAtIsNotCorrectException;
import org.acme.domain.exceptions.giftrequest.AwaitingGiftRequestWasNotFound;
import org.acme.domain.repo.GiftRequestRepository;
import org.acme.domain.service.GiftRequestService;
import org.acme.domain.service.ShortenedUrlService;
import org.acme.domain.validator.GiftRequestIdValidator;
import org.acme.domain.validator.UniqueIdValidator;
import org.acme.domain.validator.UserIdValidator;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class GiftRequestUseCases {
    private final ShortenedUrlService shortenedUrlService;
    private final GiftRequestService giftRequestService;
    private final GiftRequestRepository repo;

    public GiftRequestUseCases(ShortenedUrlService shortenedUrlService, GiftRequestService giftRequestService, GiftRequestRepository repo) {
        this.shortenedUrlService = shortenedUrlService;
        this.giftRequestService = giftRequestService;
        this.repo = repo;
    }

    public Either<GetAwaitingGiftRequestError, GiftRequest> getAwaitingGiftRequest(
            String userId,
            String uniqueIdentifier
    ) {
        var errors = new ArrayList<DomainException>();
        UniqueIdValidator.validate(uniqueIdentifier).map(errors::add);
        var validatedUserId = UserIdValidator.validate(userId).mapLeft(errors::add);
        if (!errors.isEmpty()) {
            return Either.left(new GetAwaitingGiftRequestError(Optional.empty(), errors));
        }

        var result = giftRequestService.getAwaitingGiftRequestByUniqueIdentifier(uniqueIdentifier, validatedUserId.get());
        if (result.isEmpty()) {
            return Either.left(new GetAwaitingGiftRequestError(Optional.of(new AwaitingGiftRequestWasNotFound(uniqueIdentifier)), List.of()));
        }
        return Either.right(result.get());
    }

    public Option<CreateGiftRequestError> createGiftRequest(
            CreateGiftRequestRequest request,
            String uniqueIdentifier,
            String sourceUserId
    ) {
        var errors = new ArrayList<DomainException>();
        var validatedSourceUserId = UserIdValidator.validate(sourceUserId).mapLeft(errors::add);
        var validatedTargetUserId = UserIdValidator.validate(request.targetUserId()).mapLeft(errors::add);
        UniqueIdValidator.validate(uniqueIdentifier).map(errors::add);
        if (!errors.isEmpty()) {
            return Option.of(
                    new CreateGiftRequestError(Optional.empty(), Optional.empty(), errors)
            );
        }
        var foundShortenedUrl = shortenedUrlService.getShortenedUrl(uniqueIdentifier, validatedSourceUserId.get());
        if (foundShortenedUrl.isEmpty()) {
            return Option.of(new CreateGiftRequestError(Optional.of(new ShortenedUrlIsNotFoundException()), Optional.empty(), List.of()));
        }
        return this.giftRequestService.createGiftRequest(
                new CreateGiftRequestCommand(foundShortenedUrl.get(), validatedTargetUserId.get())
        ).map(err -> new CreateGiftRequestError(Optional.empty(), Optional.of(err), List.of()));
    }

    public Option<CancelAwaitingGiftRequestError> cancelGiftRequest(
            CancelAwaitingGiftRequestRequest request,
            String uniqueIdentifier,
            String userId,
            String giftRequestId
    ) {
        var errors = new ArrayList<DomainException>();
        var userIdValidation = UserIdValidator.validate(userId).mapLeft(errors::add);
        var giftRequestIdValidation = GiftRequestIdValidator.validate(giftRequestId).mapLeft(errors::add);
        UniqueIdValidator.validate(uniqueIdentifier).map(errors::add);
        OffsetDateTime parsedUpdatedAt = null;
        try {
            parsedUpdatedAt = OffsetDateTime.parse(request.updatedAt());
        } catch (DateTimeParseException e) {
            errors.add(new UpdatedAtIsNotCorrectException(request.updatedAt()));
        }

        if (!errors.isEmpty()) {
            return Option.of(new CancelAwaitingGiftRequestError(Optional.empty(), Optional.empty(), errors));
        }

        var giftRequest = repo.getGiftRequestById(giftRequestIdValidation.get(), userIdValidation.get());

        if (giftRequest.isEmpty() || !giftRequest.get().getPublicIdentifier().equals(giftRequestId)) {
            return Option.of(
                    new CancelAwaitingGiftRequestError(
                            Optional.of(new AwaitingGiftRequestWasNotFound(uniqueIdentifier)), Optional.empty(), List.of()
                    )
            );
        }

        giftRequestService.cancelAwaitingGiftRequest(
                new CancelAwaitingGiftRequestCommand(giftRequest.get(), userIdValidation.get(), parsedUpdatedAt)
        );
        return Option.none();
    }
}
