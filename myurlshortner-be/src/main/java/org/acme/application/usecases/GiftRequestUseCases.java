package org.acme.application.usecases;

import io.vavr.control.Option;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.application.controller.giftrequest.CreateGiftRequestRequest;
import org.acme.application.exception.giftrequest.CreateGiftRequestError;
import org.acme.domain.command.CreateGiftRequestCommand;
import org.acme.domain.exceptions.DomainException;
import org.acme.domain.exceptions.ShortenedUrlIsNotFoundException;
import org.acme.domain.service.GiftRequestService;
import org.acme.domain.service.ShortenedUrlService;
import org.acme.domain.validator.UniqueIdValidator;
import org.acme.domain.validator.UserIdValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class GiftRequestUseCases {
    private final ShortenedUrlService shortenedUrlService;
    private final GiftRequestService giftRequestService;

    public GiftRequestUseCases(ShortenedUrlService shortenedUrlService, GiftRequestService giftRequestService) {
        this.shortenedUrlService = shortenedUrlService;
        this.giftRequestService = giftRequestService;
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
}
