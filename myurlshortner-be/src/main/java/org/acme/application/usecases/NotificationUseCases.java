package org.acme.application.usecases;

import io.vavr.control.Either;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.domain.entity.Notification;
import org.acme.domain.exceptions.notification.GetLatestNotificationsError;
import org.acme.domain.service.NotificationService;
import org.acme.domain.validator.UserIdValidator;

import java.util.List;

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
}
