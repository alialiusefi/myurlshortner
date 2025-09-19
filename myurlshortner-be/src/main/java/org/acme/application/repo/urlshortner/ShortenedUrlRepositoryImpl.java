package org.acme.application.repo.urlshortner;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.acme.domain.entity.ShortenedUrl;
import org.acme.domain.query.AvailableShortenedUrlWithAccessCount;
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
            throw new SaveShortenedUrlError(true);
        } else {
            this.persist(
                    new ShortenedUrlEntity(
                            shortenedUrl.getPublicIdentifier(),
                            shortenedUrl.getOriginalUrl().toString(),
                            shortenedUrl.getCreatedAt()
                    )
            );
        }
    }

    @Override
    public Optional<ShortenedUrl> getShortenedUrl(@NonNull String uniqueIdentifier) {
        return find("uniqueIdentifier = ?1", uniqueIdentifier).firstResultOptional().map(
                result -> new ShortenedUrl(
                        URI.create(result.getOriginalUrl()),
                        result.getUniqueIdentifier()
                )
        );
    }

    @Transactional
    public void cleanup() {
        deleteAll();
    }

    public Tuple2<Long, List<ShortenedUrl>> listAvailableShortenedUrls(@NonNull Integer page, @NonNull Integer size) {
        var query = find("", Sort.by("createdAt"));
        var count = query.count();
        var list = query.page(page - 1, size).list().stream().map(ent ->
                new ShortenedUrl(ent.getOriginalUrl(), ent.getUniqueIdentifier(), ent.getCreatedAt())
        ).toList();
        return new Tuple2<>(count, list);
    }

    @Override
    public Tuple2<Long, List<AvailableShortenedUrlWithAccessCount>> listAvailableShortenedUrls(
            @NonNull Integer page,
            @NonNull Integer size,
            boolean isAscending
    ) {
        var order = isAscending ? "asc" : "desc";
        var queryForCount = """
                select count(*) from (
                select us1.unique_identifier, us1.original_url, count(us2.unique_identifier), us1.created_at from shortened_urls us1 \s
                left join shortened_url_user_access us2 on us1.unique_identifier = us2.unique_identifier \s
                group by us1.unique_identifier, us1.original_url, us1.created_at \s
                );
                """;
        var query = String.format("""
                select us1.unique_identifier, us1.original_url, count(us2.unique_identifier), us1.created_at from shortened_urls us1 \s
                left join shortened_url_user_access us2 on us1.unique_identifier = us2.unique_identifier \s
                group by us1.unique_identifier, us1.original_url, us1.created_at \s
                order by us1.created_at %s limit ?1 offset ?2
                """, order);
        var count = (Long) getEntityManager().createNativeQuery(queryForCount).getSingleResult();
        var preparedStatement = getEntityManager().createNativeQuery(query);
        preparedStatement.setParameter(1, size);
        preparedStatement.setParameter(2, (page - 1) * size);
        var result = ((List<Object[]>) preparedStatement.getResultList()).stream().map(
                array -> new AvailableShortenedUrlWithAccessCount(
                        new ShortenedUrl(
                                (String) array[1],
                                (String) array[0],
                                OffsetDateTime.ofInstant((Instant) array[3], ZoneId.systemDefault())
                        ),
                        (Long) array[2]
                )
        ).toList();
        return Tuple.of(count, result);
    }
}
