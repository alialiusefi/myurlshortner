package org.acme.application.repo.urlshortner;

import jakarta.inject.Singleton;
import org.acme.domain.ShortenedUrl;
import org.acme.domain.repo.SaveShortenedUrlError;
import org.acme.domain.repo.ShortenedUrlRepository;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;


@Singleton
public class ShortenedUrlRepositoryImpl implements ShortenedUrlRepository {
    final ConcurrentHashMap<String, ShortenedUrl> data;

    ShortenedUrlRepositoryImpl() {
        this.data = new ConcurrentHashMap<>();
    }

    @Override
    public void insertShortenedUrl(ShortenedUrl shortenedUrl) throws SaveShortenedUrlError {
        if (data.contains(shortenedUrl)) {
            throw new SaveShortenedUrlError(true);
        } else {
            data.put(shortenedUrl.getPublicIdentifier(), shortenedUrl);
        }
    }

    @Override
    public @Nullable ShortenedUrl getShortenedUrl(@NonNull String uniqueIdentifier) {
        return data.get(uniqueIdentifier);
    }
}
