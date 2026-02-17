package org.acme.application.repo.urlshortner;

import io.vavr.Tuple2;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import org.acme.domain.projection.AvailableShortenedUrl;
import org.acme.domain.repo.ShortenedUrlReadRepository;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@ApplicationScoped
public class ShortenedUrlReadRepositoryImpl implements ShortenedUrlReadRepository {

    private final EntityManager em;

    public ShortenedUrlReadRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    public Tuple2<Long, List<AvailableShortenedUrl>> getAvailableShortenedUrlsByTitle(
            @NonNull List<String> titleKeywords,
            @Nullable Long userId,
            @NonNull Integer size,
            @NonNull Integer page
    ) {
        var join = """
                select us1.unique_identifier, count(acc.unique_identifier) as access_count, us1.title,
                us1.original_url, us1.is_enabled, us1.created_at from shortened_urls us1
                left join shortened_urls_indexed idx on us1.unique_identifier = idx.unique_identifier
                left join shortened_url_user_access acc on us1.unique_identifier = acc.unique_identifier
                where %s
                group by %s
                """.stripIndent();

        String whereClause = (titleKeywords.isEmpty() ? "us1.title = '' or us1.title is null" : "to_tsquery(:orquery) @@ idx.title or to_tsquery(:followquery) @@ idx.title")
                + (userId == null ? "" : String.format(" and us1.user_id = %s", userId));
        String groupByClause = !titleKeywords.isEmpty() ? "us1.unique_identifier, idx.title" : "us1.unique_identifier";
        String query = String.format(join, whereClause, groupByClause);

        String limitOffset = String.format(" limit %s offset %s", size, (page - 1) * size);


        if (titleKeywords.isEmpty()) {
            String paginated = query + limitOffset;
            String count = String.format("select count(*) from (%s)", query);

            List<AvailableShortenedUrl> urls = em.createNativeQuery(paginated).getResultList().stream()
                    .map((Object a) -> toAvailableShortenedUrl((Object[]) a)).toList();
            var paginatedCount = (Long) em.createNativeQuery(count).getSingleResult();

            return new Tuple2<>(paginatedCount, urls);
        } else {
            String orderBy = " order by ts_rank(idx.title, to_tsquery(:orquery)) + ts_rank(idx.title, to_tsquery(:followquery)) desc";
            String paginated = query + orderBy + " " + limitOffset;
            String count = String.format("select count(*) from (%s)", query);

            var paginatedQuery = em.createNativeQuery(paginated);
            String orQuery = titleKeywords.stream()
                    .reduce(new StringBuilder(), (a, b) -> a.isEmpty() ? a.append(String.format("%s", b)) : a.append(String.format(" | %s", b)), StringBuilder::append)
                    .toString();
            String followedQuery = titleKeywords.stream()
                    .reduce(new StringBuilder(), (a, b) -> a.isEmpty() ? a.append(String.format("%s", b)) : a.append(String.format(" <-> %s", b)), StringBuilder::append)
                    .toString();
            paginatedQuery.setParameter("orquery", orQuery);
            paginatedQuery.setParameter("followquery", followedQuery);
            List<AvailableShortenedUrl> urls = paginatedQuery.getResultList().stream()
                    .map((Object a) -> toAvailableShortenedUrl((Object[]) a)).toList();
            var countQuery = em.createNativeQuery(count);
            countQuery.setParameter("orquery", orQuery);
            countQuery.setParameter("followquery", followedQuery);
            var paginatedCount = (Long) countQuery.getSingleResult();

            return new Tuple2<>(paginatedCount, urls);
        }
    }

    public Tuple2<Long, List<AvailableShortenedUrl>> getAvailableShortenedUrls(
            @NonNull Integer page,
            @NonNull Integer size,
            boolean isAscending,
            @Nullable Long userId
    ) {
        String userIdPredicate = userId == null ? "" : String.format("and us1.user_id = %s", userId);
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
                isAscending ? "asc" : "desc"
        );
        var paginatedQuery = em.createNativeQuery(sql + limitAndOffset);
        paginatedQuery.setParameter("limit", size);
        paginatedQuery.setParameter("offset", size * (page - 1));
        List<AvailableShortenedUrl> list = paginatedQuery.getResultList().stream()
                .map(a -> toAvailableShortenedUrl((Object[]) a)).toList();
        var countQuery = em.createNativeQuery(String.format("select count(*) from (%s)", sql));
        Long count = (Long) countQuery.getSingleResult();
        return new Tuple2<>(count, list);
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
