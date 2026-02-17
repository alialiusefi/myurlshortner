package org.acme.application.repo.urlshortner;

import io.vavr.Tuple2;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import org.acme.domain.projection.AvailableShortenedUrl;
import org.acme.domain.query.GetAvailableShortenedUrlsQuery;
import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class ShortenedUrlReadRepositoryImpl {

    private final EntityManager em;

    public ShortenedUrlReadRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    public Tuple2<Long, List<AvailableShortenedUrl>> getAvailableShortenedUrls(
            GetAvailableShortenedUrlsQuery query
    ) {
        if (query.title() != null) {
            String[] arr = query.title().split(" ");
            String orQuery = Arrays.stream(arr)
                    .reduce(new StringBuilder(), (a, b) -> a.isEmpty() ? a.append(String.format("%s", b)) : a.append(String.format(" | %s", b)), StringBuilder::append)
                    .toString();
            String followedQuery = Arrays.stream(arr)
                    .reduce(new StringBuilder(), (a, b) -> a.isEmpty() ? a.append(String.format("%s", b)) : a.append(String.format(" <-> %s", b)), StringBuilder::append)
                    .toString();
            String userIdPredicate = query.userId() == null ? "" : String.format("and us1.user_id = %s", query.userId());
            String limitAndOffset = " limit :limit offset :offset";
            var sql = String.format(
                    """
                            select us1.unique_identifier, count(acc.unique_identifier) as access_count, us1.title,
                            us1.original_url, us1.is_enabled, us1.created_at from shortened_urls us1
                            left join shortened_urls_indexed idx on us1.unique_identifier = idx.unique_identifier
                            left join shortened_url_user_access acc on us1.unique_identifier = acc.unique_identifier
                            where to_tsquery(:orquery) @@ idx.title or to_tsquery(:followquery) @@ idx.title %s
                            group by us1.unique_identifier, idx.title
                            order by ts_rank(idx.title, to_tsquery(:orquery)) + ts_rank(idx.title, to_tsquery(:followquery)) desc
                            """.stripIndent(),
                    userIdPredicate
            );
            var paginatedQuery = em.createNativeQuery(sql + limitAndOffset);
            paginatedQuery.setParameter("orquery", orQuery);
            paginatedQuery.setParameter("followquery", followedQuery);
            paginatedQuery.setParameter("limit", query.size());
            paginatedQuery.setParameter("offset", query.size() * (query.page() - 1));
            List<AvailableShortenedUrl> list = paginatedQuery.getResultList().stream()
                    .map(a -> toAvailableShortenedUrl((Object[]) a)).toList();
            var countQuery = em.createNativeQuery(String.format("select count(*) from (%s)", sql));
            countQuery.setParameter("orquery", orQuery);
            countQuery.setParameter("followquery", followedQuery);
            Long count = (Long) countQuery.getSingleResult();
            return new Tuple2<>(count, list);
        } else {
            String userIdPredicate = query.userId() == null ? "" : String.format("and us1.user_id = %s", query.userId());
            String limitAndOffset = " limit :limit offset :offset";
            var sql = String.format(
                    """
                            select us1.unique_identifier, count(acc.unique_identifier) as access_count, us1.title,
                            us1.original_url, us1.is_enabled, us1.created_at from shortened_urls us1
                            left join shortened_url_user_access acc on us1.unique_identifier = acc.unique_identifier %s
                            group by us1.unique_identifier
                            order by us1.created_at %s
                            """.stripIndent(),
                    userIdPredicate,
                    query.isAscending() ? "asc" : "desc"
            );
            var paginatedQuery = em.createNativeQuery(sql + limitAndOffset);
            paginatedQuery.setParameter("limit", query.size());
            paginatedQuery.setParameter("offset", query.size() * (query.page() - 1));
            List<AvailableShortenedUrl> list = paginatedQuery.getResultList().stream()
                    .map(a -> toAvailableShortenedUrl((Object[]) a)).toList();
            var countQuery = em.createNativeQuery(String.format("select count(*) from (%s)", sql));
            Long count = (Long) countQuery.getSingleResult();
            return new Tuple2<>(count, list);
        }
    }

    private AvailableShortenedUrl toAvailableShortenedUrl(Object[] resultSet) {
        return new AvailableShortenedUrl(
                (String) resultSet[0],
                URI.create((String) resultSet[3]),
                OffsetDateTime.ofInstant((Instant) resultSet[5], ZoneId.systemDefault()),
                (Long) resultSet[1],
                (Boolean) resultSet[4],
                (String) resultSet[2]
        );
    }
}
