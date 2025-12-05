package org.acme.application.repo.notification;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.acme.domain.entity.NotificationType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "notifications")
public class NotificationEntity extends PanacheEntityBase {
    @Id
    private Long id;
    private String uniqueIdentifier;
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    @JdbcTypeCode(SqlTypes.JSON)
    private String params;
    private Long userId;
    private OffsetDateTime createdAt;
    private OffsetDateTime readAt;

    public NotificationEntity() {
    }

    public NotificationEntity(Long id, String uniqueIdentifier,
                              NotificationType type, String params,
                              Long userId, OffsetDateTime createdAt,
                              OffsetDateTime readAt
    ) {
        this.id = id;
        this.uniqueIdentifier = uniqueIdentifier;
        this.type = type;
        this.params = params;
        this.userId = userId;
        this.createdAt = createdAt;
        this.readAt = readAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(OffsetDateTime readAt) {
        this.readAt = readAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public void setUniqueIdentifier(String uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NotificationEntity that = (NotificationEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(uniqueIdentifier, that.uniqueIdentifier) && type == that.type && Objects.equals(params, that.params) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uniqueIdentifier, type, params, userId);
    }
}
