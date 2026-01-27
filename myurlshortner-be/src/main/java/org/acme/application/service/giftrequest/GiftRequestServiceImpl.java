package org.acme.application.service.giftrequest;

import io.vavr.control.Option;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.SystemException;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.Transactional;
import org.acme.application.repo.exception.DuplicateAwaitingGiftRequestException;
import org.acme.application.repo.notification.NotificationRepository;
import org.acme.domain.command.AcceptAwaitingGiftRequestCommand;
import org.acme.domain.command.CancelAwaitingGiftRequestCommand;
import org.acme.domain.command.CreateGiftRequestCommand;
import org.acme.domain.command.DeclineAwaitingGiftRequestCommand;
import org.acme.domain.entity.GiftRequest;
import org.acme.domain.entity.Notification;
import org.acme.domain.entity.NotificationParams;
import org.acme.domain.entity.NotificationType;
import org.acme.domain.exceptions.giftrequest.CancelAwaitingGiftRequestError;
import org.acme.domain.exceptions.giftrequest.CreateGiftRequestError;
import org.acme.domain.exceptions.giftrequest.GiftRequestWasUpdatedException;
import org.acme.domain.repo.GiftRequestRepository;
import org.acme.domain.service.GiftRequestService;
import org.acme.domain.service.ShortenedUrlService;
import org.jspecify.annotations.NonNull;

import java.time.OffsetDateTime;

@ApplicationScoped
public class GiftRequestServiceImpl implements GiftRequestService {

    private final GiftRequestRepository repo;
    private final NotificationRepository notificationRepository;
    private final ShortenedUrlService shortenedUrlService;
    private final TransactionManager manager;

    public GiftRequestServiceImpl(
            GiftRequestRepository repo,
            NotificationRepository notificationRepository,
            ShortenedUrlService shortenedUrlService,
            TransactionManager manager
    ) {
        this.repo = repo;
        this.notificationRepository = notificationRepository;
        this.shortenedUrlService = shortenedUrlService;
        this.manager = manager;
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
                        OffsetDateTime.now(),
                        null
                ));
                notificationRepository.saveNotification(
                        new Notification(
                                null,
                                command.shortenedUrl().getPublicIdentifier(),
                                NotificationType.GIFT_REQUEST_TO_TARGET_USER,
                                new NotificationParams.GiftRequestToTargetUserParams(
                                        command.shortenedUrl().getPublicIdentifier(),
                                        giftRequestId,
                                        command.shortenedUrl().getUserId()
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
        var error = repo.updateGiftRequestStatusByIdAndUpdatedAt(
                command.giftRequest().getId(),
                GiftRequest.GiftRequestStatus.CANCELED,
                command.updatedAt()
        ).map(CancelAwaitingGiftRequestError::new);
        if (!error.isEmpty()) {
            return error;
        }
        notificationRepository.deleteGiftRequestToTargetUserNotificationByGiftRequestIdParam(command.giftRequest().getId());
        return Option.none();
    }

    @Override
    @Transactional
    public Option<CancelAwaitingGiftRequestError> cancelExpiredGiftRequest(
            @NonNull CancelAwaitingGiftRequestCommand command
    ) {
        var error = cancelAwaitingGiftRequest(command);
        if (!error.isEmpty()) {
            return error;
        }
        notificationRepository.saveNotification(
                new Notification(
                        null,
                        command.giftRequest().getPublicIdentifier(),
                        NotificationType.GIFT_REQUEST_RESPONSE_TO_SOURCE_USER,
                        new NotificationParams.GiftRequestResponseToSourceUserParams(
                                command.giftRequest().getId(),
                                command.giftRequest().getTargetUserId(),
                                NotificationParams.GiftRequestResponseToSourceUserParams.GiftRequestResponseToSourceUserType.EXPIRED,
                                command.giftRequest().getPublicIdentifier()
                        ),
                        command.giftRequest().getSourceUserId(),
                        OffsetDateTime.now(),
                        null
                )
        );
        return Option.none();
    }

    @Override
    @Transactional
    public Option<GiftRequestWasUpdatedException> acceptAwaitingGiftRequest(
            @NonNull AcceptAwaitingGiftRequestCommand command
    ) {
        var giftRequest = repo.getGiftRequestByIdAndStatus(
                command.giftRequestId(),
                GiftRequest.GiftRequestStatus.AWAITING,
                null
        ).get();
        shortenedUrlService.giftShortenedUrl(
                giftRequest.getPublicIdentifier(),
                giftRequest.getTargetUserId()
        );
        notificationRepository.deleteGiftRequestToTargetUserNotificationByGiftRequestIdParam(command.giftRequestId());
        var error = repo.updateGiftRequestStatusByIdAndUpdatedAt(
                giftRequest.getId(),
                GiftRequest.GiftRequestStatus.ACCEPTED,
                giftRequest.getUpdatedAt()
        );
        if (!error.isEmpty()) {
            try {
                manager.getTransaction().rollback();
                return error;
            } catch (SystemException r) {
                throw new IllegalStateException("Unexpected error");
            }
        }
        notificationRepository.saveNotification(
                new Notification(
                        null,
                        giftRequest.getPublicIdentifier(),
                        NotificationType.GIFT_REQUEST_RESPONSE_TO_SOURCE_USER,
                        new NotificationParams.GiftRequestResponseToSourceUserParams(
                                giftRequest.getId(),
                                giftRequest.getTargetUserId(),
                                NotificationParams.GiftRequestResponseToSourceUserParams.GiftRequestResponseToSourceUserType.ACCEPTED,
                                giftRequest.getPublicIdentifier()
                        ),
                        giftRequest.getSourceUserId(),
                        OffsetDateTime.now(),
                        null
                )
        );
        return Option.none();
    }

    @Override
    @Transactional
    public Option<GiftRequestWasUpdatedException> declineAwaitingGiftRequest(
            @NonNull DeclineAwaitingGiftRequestCommand command
    ) {
        notificationRepository.deleteGiftRequestToTargetUserNotificationByGiftRequestIdParam(command.giftRequest().getId());
        var error = repo.updateGiftRequestStatusByIdAndUpdatedAt(
                command.giftRequest().getId(),
                GiftRequest.GiftRequestStatus.DECLINED,
                command.updatedAt()
        );
        if (!error.isEmpty()) {
            try {
                manager.getTransaction().rollback();
                return error;
            } catch (SystemException r) {
                throw new IllegalStateException("Unexpected error");
            }
        }
        notificationRepository.saveNotification(
                new Notification(
                        null,
                        command.giftRequest().getPublicIdentifier(),
                        NotificationType.GIFT_REQUEST_RESPONSE_TO_SOURCE_USER,
                        new NotificationParams.GiftRequestResponseToSourceUserParams(
                                command.giftRequest().getId(),
                                command.giftRequest().getTargetUserId(),
                                NotificationParams.GiftRequestResponseToSourceUserParams.GiftRequestResponseToSourceUserType.DECLINED,
                                command.giftRequest().getPublicIdentifier()
                        ),
                        command.giftRequest().getSourceUserId(),
                        OffsetDateTime.now(),
                        null
                )
        );
        return Option.none();
    }
}
