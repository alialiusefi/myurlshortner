package org.acme.application.repo.urlshortner;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ShortenedUrlIndexedRepositoryImpl {

    private final EntityManager entityManager;

    public ShortenedUrlIndexedRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    void upsertShortenedUrlIndexed(String uniqueIdentifier, String title) {
        var query = entityManager.createNativeQuery(
                """
                        insert into shortened_urls_indexed (unique_identifier, title) values (?1, to_tsvector(?2))
                        on conflict (unique_identifier) do update set title = to_tsvector(?2)
                        """.stripIndent()
        );
        query.setParameter(1, uniqueIdentifier);
        query.setParameter(2, title);
        query.executeUpdate();
    }
}
