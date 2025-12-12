package org.acme.application.repo.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.vavr.control.Option;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.domain.entity.Notification;
import org.acme.domain.entity.NotificationParams;
import org.acme.domain.exceptions.NotificationIsNotFound;
import org.jspecify.annotations.NonNull;

import java.time.OffsetDateTime;
import java.util.List;

@ApplicationScoped
public class NotificationRepository implements PanacheRepository<NotificationEntity> {

    @Inject
    private ObjectMapper mapper;

    public List<Notification> getLatestNotificationsByUserId(@NonNull Long userId, @NonNull Integer size) {
        return find("userId = ?1 order by createdAt desc", userId)
                .page(0, size)
                .list()
                .stream()
                .map(this::toNotification)
                .toList();
    }

    public Notification toNotification(NotificationEntity entity) {
        NotificationParams params = null;
        try {
            switch (entity.getType()) {
                case SHORTENED_URL_REACHED_N_VIEWS ->
                        params = mapper.readValue(entity.getParams(), NotificationParams.ShortenedUrlReachedNViewsParams.class);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return new Notification(
                entity.getId(),
                entity.getUniqueIdentifier(),
                entity.getType(),
                params,
                entity.getUserId(),
                entity.getCreatedAt(),
                entity.getReadAt()
        );
    }

    @Transactional
    public int setNotificationReadAtByIdAndUserId(@NonNull OffsetDateTime readAt,
                                                   @NonNull Long notificationId,
                                                   @NonNull Long userId
    ) {
        return update("set readAt = ?1 where id = ?2 and userId = ?3", readAt, notificationId, userId);
    }
}
