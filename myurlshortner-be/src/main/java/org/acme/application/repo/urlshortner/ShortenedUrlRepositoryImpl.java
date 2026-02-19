package org.acme.application.repo.urlshortner;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.acme.application.repo.exception.ShortenedUrlOptimisticLockException;
import org.acme.domain.entity.ShortenedUrl;
import org.acme.domain.repo.SaveShortenedUrlConflictError;
import org.acme.domain.repo.ShortenedUrlRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.Optional;


@Singleton
public class ShortenedUrlRepositoryImpl implements ShortenedUrlRepository, PanacheRepository<ShortenedUrlEntity> {

    private final ShortenedUrlIndexedRepositoryImpl indexed;

    public ShortenedUrlRepositoryImpl(ShortenedUrlIndexedRepositoryImpl indexed) {
        this.indexed = indexed;
    }

    @Override
    @Transactional
    public void insertShortenedUrl(@NonNull ShortenedUrl shortenedUrl) throws SaveShortenedUrlConflictError {
        try {
            this.persistAndFlush(toEntity(shortenedUrl));
        } catch (ConstraintViolationException e) {
            throw new SaveShortenedUrlConflictError(shortenedUrl.getPublicIdentifier());

        }
        indexed.upsertShortenedUrlIndexed(shortenedUrl.getPublicIdentifier(), shortenedUrl.getTitle());
    }

    @Override
    public Optional<ShortenedUrl> getShortenedUrl(@NonNull String uniqueIdentifier, @Nullable Long userId) {
        var query = userId == null ?
                find("uniqueIdentifier = ?1", uniqueIdentifier) :
                find("uniqueIdentifier = ?1 and userId = ?2", uniqueIdentifier, userId);
        return query.firstResultOptional().map(
                result -> new ShortenedUrl(
                        result.getOriginalUrl(),
                        result.getUniqueIdentifier(),
                        result.getCreatedAt(),
                        result.getUpdatedAt(),
                        result.getEnabled(),
                        result.getUserId(),
                        result.getTitle()
                )
        );
    }

    @Transactional
    public void cleanup() {
        deleteAll();
    }

    @Override
    @Transactional
    public void updateShortenedUrl(@NonNull ShortenedUrl shortenedUrl, OffsetDateTime existingUpdatedAt) throws ShortenedUrlOptimisticLockException {
        var count = update("set originalUrl = ?1, updatedAt = ?2, isEnabled = ?3, userId = ?4, title = ?5 where uniqueIdentifier = ?6 and updatedAt = ?7",
                shortenedUrl.getOriginalUrl().toString(),
                shortenedUrl.getUpdatedAt(),
                shortenedUrl.isEnabled(),
                shortenedUrl.getUserId(),
                shortenedUrl.getTitle(),
                shortenedUrl.getPublicIdentifier(),
                existingUpdatedAt
        );
        if (count != 1) {
            throw new ShortenedUrlOptimisticLockException(shortenedUrl.getPublicIdentifier(), existingUpdatedAt);
        }
        indexed.upsertShortenedUrlIndexed(shortenedUrl.getPublicIdentifier(), shortenedUrl.getTitle());
    }

    private ShortenedUrlEntity toEntity(ShortenedUrl from) {
        return new ShortenedUrlEntity(
                from.getPublicIdentifier(),
                from.getOriginalUrl().toString(),
                from.getCreatedAt(),
                from.getUpdatedAt(),
                from.isEnabled(),
                from.getUserId(),
                from.getTitle()
        );
    }
}
