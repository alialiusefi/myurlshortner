package org.acme.application.repo.urlshortner;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.smallrye.common.constraint.NotNull;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.acme.application.repo.exception.ShortenedUrlOptimisticLockException;
import org.acme.domain.entity.ShortenedUrl;
import org.acme.domain.projection.AvailableShortenedUrl;
import org.acme.domain.repo.SaveShortenedUrlError;
import org.acme.domain.repo.ShortenedUrlRepository;
import org.jspecify.annotations.NonNull;

import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;


@Singleton
public class ShortenedUrlRepositoryImpl implements ShortenedUrlRepository, PanacheRepository<ShortenedUrlEntity> {

    @Override
    @Transactional
    public void insertShortenedUrl(@NonNull ShortenedUrl shortenedUrl) throws SaveShortenedUrlError {
        if (this.getShortenedUrl(shortenedUrl.getPublicIdentifier()).isPresent()) {
            throw new SaveShortenedUrlError(shortenedUrl.getPublicIdentifier());
        } else {
            this.persist(
                    toEntity(shortenedUrl)
            );
        }
    }

    @Override
    public Optional<ShortenedUrl> getShortenedUrl(@NonNull String uniqueIdentifier) {
        return find("uniqueIdentifier = ?1", uniqueIdentifier).firstResultOptional().map(
                result -> new ShortenedUrl(
                        result.getOriginalUrl(),
                        result.getUniqueIdentifier(),
                        result.getCreatedAt(),
                        result.getUpdatedAt(),
                        result.getEnabled()
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
            boolean isAscending
    ) {
        var order = isAscending ? "asc" : "desc";
        var queryForCount = """
                select count(*) from (
                select us1.unique_identifier, us1.original_url, count(us2.unique_identifier), us1.created_at, us1.is_enabled from shortened_urls us1 \s
                left join shortened_url_user_access us2 on us1.unique_identifier = us2.unique_identifier \s
                group by us1.unique_identifier, us1.original_url, us1.created_at, us1.is_enabled \s
                );
                """;
        var query = String.format("""
                select us1.unique_identifier, us1.original_url, count(us2.unique_identifier), us1.created_at, us1.is_enabled from shortened_urls us1 \s
                left join shortened_url_user_access us2 on us1.unique_identifier = us2.unique_identifier \s
                group by us1.unique_identifier, us1.original_url, us1.created_at, us1.is_enabled \s
                order by us1.created_at %s limit ?1 offset ?2
                """, order);
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
                                (Boolean) array[4]
                )
        ).toList();
        return Tuple.of(count, result);
    }

    @Override
    @Transactional
    public @NotNull void updateShortenedUrl(@NonNull ShortenedUrl shortenedUrl, OffsetDateTime existingUpdatedAt) throws ShortenedUrlOptimisticLockException {
        var count = update("set originalUrl = ?1, updatedAt = ?2, isEnabled = ?3 where uniqueIdentifier = ?4 and updatedAt = ?5",
                shortenedUrl.getOriginalUrl().toString(),
                shortenedUrl.getUpdatedAt(),
                shortenedUrl.isEnabled(),
                shortenedUrl.getPublicIdentifier(),
                existingUpdatedAt
        );
        if (count != 1) {
            throw new ShortenedUrlOptimisticLockException(shortenedUrl.getPublicIdentifier(), existingUpdatedAt);
        }
    }

    private ShortenedUrlEntity toEntity(ShortenedUrl from) {
        return new ShortenedUrlEntity(from.getPublicIdentifier(), from.getOriginalUrl().toString(), from.getCreatedAt(), from.getUpdatedAt(), from.isEnabled());
    }
}
