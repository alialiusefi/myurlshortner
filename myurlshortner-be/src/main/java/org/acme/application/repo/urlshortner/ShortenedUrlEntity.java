package org.acme.application.repo.urlshortner;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "shortened_urls")
public class ShortenedUrlEntity extends PanacheEntityBase {
    @Id
    @Column(name = "unique_identifier")
    private String uniqueIdentifier;
    @Column(name = "original_url")
    private String originalUrl;
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    public ShortenedUrlEntity() {
    }

    public ShortenedUrlEntity(String uniqueIdentifier, String originalUrl, OffsetDateTime createdAt) {
        this.uniqueIdentifier = uniqueIdentifier;
        this.originalUrl = originalUrl;
        this.createdAt = createdAt;
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
}
