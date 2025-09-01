package org.acme.application.repo.urlshortner;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import jakarta.inject.Singleton;
import org.acme.domain.ShortenedUrl;
import org.acme.domain.repo.SaveShortenedUrlError;
import org.acme.domain.repo.ShortenedUrlRepository;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Singleton
public class ShortenedUrlRepositoryImpl implements ShortenedUrlRepository {
    final ConcurrentHashMap<String, ShortenedUrl> data;

    ShortenedUrlRepositoryImpl() {
        this.data = new ConcurrentHashMap<>();
        this.data.put("abcdefghik", new ShortenedUrl(URI.create("https://www.google.com"), "abcdefghik"));
    }

    @Override
    public void insertShortenedUrl(@NonNull ShortenedUrl shortenedUrl) throws SaveShortenedUrlError {
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

    public void cleanup() {
        data.clear();
        data.put("abcdefghik", new ShortenedUrl(URI.create("https://www.google.com"), "abcdefghik"));
    }

    public Tuple2<Long, List<ShortenedUrl>> listAvailableShortenedUrls(@NonNull Integer page, @NonNull Integer size) {
        return Tuple.of(
                Integer.toUnsignedLong(data.size()),
                data.entrySet().stream()
                        .skip((long) (page - 1) * size)
                        .limit(size)
                        .map((Map.Entry::getValue)).toList()
        );
    }
}
