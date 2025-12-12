package org.acme.application.usecases;

import io.vavr.control.Either;
import io.vavr.control.Option;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.application.exception.notification.ReadNotificationError;
import org.acme.domain.entity.Notification;
import org.acme.application.exception.notification.GetLatestNotificationsError;
import org.acme.domain.exceptions.DomainException;
import org.acme.domain.service.NotificationService;
import org.acme.domain.validator.NotificationIdValidator;
import org.acme.domain.validator.UserIdValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class NotificationUseCases {
    private final NotificationService notificationService;

    public NotificationUseCases(NotificationService service) {
        this.notificationService = service;
    }

    public Either<GetLatestNotificationsError, List<Notification>> getLatestNotifications(String userId) {
        var userValidation = UserIdValidator.validate(userId);
        if (userValidation.isLeft()) {
            return Either.left(new GetLatestNotificationsError(userValidation.getLeft()));
        }

        return Either.right(notificationService.getLatestNotifications(userValidation.get()));
    }

    public Option<ReadNotificationError> readNotification(String userId, String notificationId) {
        var errors = new ArrayList<DomainException>();
        var userValidation = UserIdValidator.validate(userId).mapLeft(errors::add);
        var notificationIdValidation = NotificationIdValidator.validate(notificationId).mapLeft(errors::add);

        if (!errors.isEmpty()) {
            return Option.of(new ReadNotificationError(errors, Optional.empty()));
        }

        return notificationService.readNotification(userValidation.get(), notificationIdValidation.get()).map(
                it -> new ReadNotificationError(List.of(), Optional.of(it))
        );
    }
}
