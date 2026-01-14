package org.acme.application.service.giftrequest;

import io.vavr.control.Option;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.acme.application.repo.exception.DuplicateAwaitingGiftRequestException;
import org.acme.application.repo.notification.NotificationRepository;
import org.acme.domain.command.CancelAwaitingGiftRequestCommand;
import org.acme.domain.command.CreateGiftRequestCommand;
import org.acme.domain.entity.GiftRequest;
import org.acme.domain.entity.Notification;
import org.acme.domain.entity.NotificationParams;
import org.acme.domain.entity.NotificationType;
import org.acme.domain.exceptions.giftrequest.CancelAwaitingGiftRequestError;
import org.acme.domain.exceptions.giftrequest.CreateGiftRequestError;
import org.acme.domain.repo.GiftRequestRepository;
import org.acme.domain.service.GiftRequestService;
import org.jspecify.annotations.NonNull;

import java.time.OffsetDateTime;

@ApplicationScoped
public class GiftRequestServiceImpl implements GiftRequestService {

    private final GiftRequestRepository repo;
    private final NotificationRepository notificationRepository;

    public GiftRequestServiceImpl(GiftRequestRepository repo, NotificationRepository notificationRepository) {
        this.repo = repo;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public Option<CreateGiftRequestError> createGiftRequest(@NonNull CreateGiftRequestCommand command) {
        if (command.shortenedUrl().getUserId().equals(command.targetUserId())) {
            return Option.of(new CreateGiftRequestError.GiftRequestTargetUserCannotBeTheSourceUser(command.targetUserId()));
        }
        var optionalGiftRequest = repo.getGiftRequestByUniqueIdentifierAndStatusIsAwaiting(command.shortenedUrl().getPublicIdentifier(), null, true);
        if (optionalGiftRequest.isEmpty()) {
            try {
                var giftRequestId = repo.saveGiftRequest(new GiftRequest(
                        command.shortenedUrl().getUserId(),
                        command.targetUserId(),
                        command.shortenedUrl().getPublicIdentifier(),
                        GiftRequest.GiftRequestStatus.AWAITING,
                        command.shortenedUrl().getCreatedAt(),
                        null
                ));
                notificationRepository.saveNotification(
                        new Notification(
                                null,
                                command.shortenedUrl().getPublicIdentifier(),
                                NotificationType.GIFT_REQUEST_TO_TARGET_USER,
                                new NotificationParams.GiftRequestToTargetUserParams(
                                        command.shortenedUrl().getPublicIdentifier(),
                                        giftRequestId
                                ),
                                command.targetUserId(),
                                OffsetDateTime.now(),
                                null
                        )
                );
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
        return Option.ofOptional(repo.getGiftRequestByUniqueIdentifierAndStatusIsAwaiting(uniqueIdentifier, userId, false));
    }

    @Override
    @Transactional
    public Option<CancelAwaitingGiftRequestError> cancelAwaitingGiftRequest(
            @NonNull CancelAwaitingGiftRequestCommand command
    ) {
        return repo.updateGiftRequestStatusByIdAndUpdatedAt(command.giftRequest().getId(), GiftRequest.GiftRequestStatus.CANCELED, command.updatedAt()).map(CancelAwaitingGiftRequestError::new);
    }

    @Transactional
    public Option<CancelAwaitingGiftRequestError> cancelExpiredGiftRequest(@NonNull GiftRequest giftRequest) {
        var error = repo.updateGiftRequestStatusByIdAndUpdatedAt(
                giftRequest.getId(),
                GiftRequest.GiftRequestStatus.CANCELED,
                giftRequest.getUpdatedAt()
        ).map(CancelAwaitingGiftRequestError::new);
        if (!error.isEmpty()) {
            return error;
        }
        notificationRepository.deleteNotificationByGiftRequestIdParam(giftRequest.getId());
        return Option.none();
    }
}
