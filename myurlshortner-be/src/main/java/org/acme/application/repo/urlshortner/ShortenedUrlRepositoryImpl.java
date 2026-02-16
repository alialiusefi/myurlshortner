package org.acme.application.repo.urlshortner;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.acme.application.repo.exception.ShortenedUrlOptimisticLockException;
import org.acme.domain.entity.ShortenedUrl;
import org.acme.domain.projection.AvailableShortenedUrl;
import org.acme.domain.repo.SaveShortenedUrlConflictError;
import org.acme.domain.repo.ShortenedUrlRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
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
    public Tuple2<Long, List<AvailableShortenedUrl>> listAvailableShortenedUrls(
            @NonNull Integer page,
            @NonNull Integer size,
            boolean isAscending,
            @Nullable Long userId
    ) {
        var whereClause = userId == null ? "" : String.format("where us1.user_id = %s", userId);
        var order = isAscending ? "asc" : "desc";
        var queryForCount = String.format("""
                select count(*) from (
                select us1.unique_identifier, us1.original_url, count(us2.unique_identifier), us1.created_at, us1.is_enabled, us1.title from shortened_urls us1 \s
                left join shortened_url_user_access us2 on us1.unique_identifier = us2.unique_identifier \s
                %s \s
                group by us1.unique_identifier, us1.original_url, us1.created_at, us1.is_enabled \s
                );
                """, whereClause);
        var query = String.format("""
                select us1.unique_identifier, us1.original_url, count(us2.unique_identifier), us1.created_at, us1.is_enabled, us1.title from shortened_urls us1 \s
                left join shortened_url_user_access us2 on us1.unique_identifier = us2.unique_identifier \s
                %s
                group by us1.unique_identifier, us1.original_url, us1.created_at, us1.is_enabled \s
                order by us1.created_at %s limit ?1 offset ?2
                """, whereClause, order);
        var count = (Long) getEntityManager().createNativeQuery(queryForCount).getSingleResult();
        var preparedStatement = getEntityManager().createNativeQuery(query);
        preparedStatement.setParameter(1, size);
        preparedStatement.setParameter(2, (page - 1) * size);
        var result = ((List<Object[]>) preparedStatement.getResultList()).stream().map(
                array -> new AvailableShortenedUrl(
                        (String) array[0],
                        URI.create((String) array[1]),
                        OffsetDateTime.ofInstant((Instant) array[3], ZoneId.systemDefault()),
                        (Long) array[2],
                        (Boolean) array[4],
                        (String) array[5]
                )
        ).toList();
        return Tuple.of(count, result);
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
