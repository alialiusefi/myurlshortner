package org.acme.domain.repo;

public interface ShortenedUrlIndexedRepository {

    void upsertShortenedUrlIndexed(String uniqueIdentifier, String title);
}
