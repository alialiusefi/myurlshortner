package org.acme.application.repo.urlshortner;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.json.JsonCommands;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.domain.entity.ShortenedUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

// https://quarkus.io/guides/redis-reference
@ApplicationScoped
public class ShortenedUrlCache {
    private static Logger logger = LoggerFactory.getLogger(ShortenedUrlCache.class);
    private final JsonCommands<String> api;

    public ShortenedUrlCache(RedisDataSource ds) {
        this.api = ds.json();
    }

    public Optional<ShortenedUrl> getByKey(String uid, Supplier<Optional<ShortenedUrl>> fromDb) {
        var cached = api.jsonGetObject(uid);
        logger.debug("Cache returned {} for uid {}", cached, uid);
        if (cached == null) {
            logger.debug("Cache missed!");
            var raw = fromDb.get();
            if (raw.isEmpty()) {
                logger.debug("Setting empty json for key {}", uid);
                api.jsonSet(uid, "$", Map.of());
                return Optional.empty();
            } else {
                api.jsonSet(uid, "$", raw.get());
                return raw;
            }
        } else if (cached.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(cached.mapTo(ShortenedUrl.class));
        }
    }

    public Optional<ShortenedUrl> getByKeyAndUserId(String uid, Long userId, Supplier<Optional<ShortenedUrl>> fromDb) {
        var optionalUrl = getByKey(uid, fromDb);
        if (optionalUrl.isPresent()) {
            if (optionalUrl.get().getUserId().equals(userId)) {
                return optionalUrl;
            } else {
                return Optional.empty();
            }
        }
        return optionalUrl;
    }

    public void put(String uid, ShortenedUrl shortenedUrl) {
        api.jsonSet(uid, shortenedUrl);
    }
}
