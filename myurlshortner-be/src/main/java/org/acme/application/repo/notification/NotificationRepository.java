package org.acme.application.repo.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.domain.entity.Notification;
import org.acme.domain.entity.NotificationParams;
import org.acme.domain.entity.NotificationType;
import org.jspecify.annotations.NonNull;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

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

    private Notification toNotification(NotificationEntity entity) {
        NotificationParams params = null;
        try {
            switch (entity.getType()) {
                case SHORTENED_URL_REACHED_N_VIEWS ->
                        params = mapper.readValue(entity.getParams(), NotificationParams.ShortenedUrlReachedNViewsParams.class);
                case GIFT_REQUEST_TO_TARGET_USER ->
                        params = mapper.readValue(entity.getParams(), NotificationParams.GiftRequestToTargetUserParams.class);
                case GIFT_REQUEST_RESPONSE_TO_SOURCE_USER ->
                        params = mapper.readValue(entity.getParams(), NotificationParams.GiftRequestResponseToSourceUserParams.class);
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

    public Optional<Notification> getNotificationById(@NonNull Long id, @NonNull Long userId) {
        return find("id = ?1 and userId = ?2", id, userId).firstResultOptional().map(this::toNotification);
    }

    @Transactional
    public int setNotificationReadAtByIdAndUserId(@NonNull OffsetDateTime readAt,
                                                  @NonNull Long notificationId,
                                                  @NonNull Long userId
    ) {
        return update("set readAt = ?1 where id = ?2 and userId = ?3 and readAt is null", readAt, notificationId, userId);
    }

    @Transactional
    public void saveNotification(Notification notification) {
        try {
            var entity = new NotificationEntity(
                    notification.id(),
                    notification.uniqueIdentifier(),
                    notification.type(),
                    mapper.writeValueAsString(notification.params()),
                    notification.userId(),
                    notification.createdAt(),
                    notification.readAt()
            );
            persist(entity);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unexpected JSON error!", e);
        }
    }

    @Transactional
    public void deleteNotificationByGiftRequestIdParam(Long giftRequestIdParam) {
        var query = getEntityManager().createNativeQuery(
                "delete from notifications where params->>'gift_request_id' = ?1 and type = ?2"
        );
        query.setParameter(1, giftRequestIdParam.toString());
        query.setParameter(2, NotificationType.GIFT_REQUEST_TO_TARGET_USER.name());
        query.executeUpdate();
    }
}
