package org.acme.application.service.giftrequest;

import io.vavr.control.Option;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.acme.application.repo.exception.DuplicateAwaitingGiftRequestException;
import org.acme.domain.command.CreateGiftRequestCommand;
import org.acme.domain.entity.GiftRequest;
import org.acme.domain.exceptions.giftrequest.CreateGiftRequestError;
import org.acme.domain.repo.GiftRequestRepository;
import org.acme.domain.service.GiftRequestService;
import org.jspecify.annotations.NonNull;

@ApplicationScoped
public class GiftRequestServiceImpl implements GiftRequestService {

    private final GiftRequestRepository repo;

    public GiftRequestServiceImpl(GiftRequestRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public Option<CreateGiftRequestError> createGiftRequest(@NonNull CreateGiftRequestCommand command) {
        if (command.shortenedUrl().getUserId().equals(command.targetUserId())) {
            return Option.of(new CreateGiftRequestError.GiftRequestTargetUserCannotBeTheSourceUser(command.targetUserId()));
        }
        var optionalGiftRequest = repo.getGiftRequestByUniqueIdentifierAndStatusIsAwaiting(command.shortenedUrl().getPublicIdentifier(), null);
        if (optionalGiftRequest.isEmpty()) {
            try {
                repo.saveGiftRequest(new GiftRequest(
                        command.shortenedUrl().getUserId(),
                        command.targetUserId(),
                        command.shortenedUrl().getPublicIdentifier(),
                        GiftRequest.GiftRequestStatus.AWAITING,
                        command.shortenedUrl().getCreatedAt(),
                        null
                ));
                return Option.none();
            } catch (DuplicateAwaitingGiftRequestException e) {
                return Option.of(new CreateGiftRequestError.ShortenedUrlAlreadyHasAGiftRequest(command.shortenedUrl().getPublicIdentifier()));
            }
        } else {
            var giftRequest = optionalGiftRequest.get();
            if (giftRequest.getTargetUserId().equals(command.targetUserId())) {
                return Option.of(new CreateGiftRequestError.TargetUserAlreadyHasSuchGiftRequest(command.shortenedUrl().getPublicIdentifier(), command.targetUserId()));
            } else {
                return Option.of(new CreateGiftRequestError.ShortenedUrlAlreadyHasAGiftRequest(command.shortenedUrl().getPublicIdentifier()));
            }
        }
    }

    @Override
    public Option<GiftRequest> getAwaitingGiftRequestByUniqueIdentifier(@NonNull String uniqueIdentifier, @NonNull Long userId) {
        return Option.ofOptional(repo.getGiftRequestByUniqueIdentifierAndStatusIsAwaiting(uniqueIdentifier, userId));
    }
}
