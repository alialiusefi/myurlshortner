package org.acme.application.repo.urlshortner;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "shortened_urls")
public class ShortenedUrlEntity extends PanacheEntityBase {
    @Id
    private String uniqueIdentifier;
    private String originalUrl;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private Boolean isEnabled;
    private Long userId;
    private String title;

    public ShortenedUrlEntity() {
    }

    public ShortenedUrlEntity(
            String uniqueIdentifier,
            String originalUrl,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt,
            Boolean isEnabled,
            Long userId,
            String title
    ) {
        this.uniqueIdentifier = uniqueIdentifier;
        this.originalUrl = originalUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isEnabled = isEnabled;
        this.userId = userId;
        this.title = title;
    }

    public String getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public void setUniqueIdentifier(String uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
