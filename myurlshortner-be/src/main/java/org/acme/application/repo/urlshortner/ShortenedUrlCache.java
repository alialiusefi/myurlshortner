package org.acme.application.repo.urlshortner;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

// https://quarkus.io/guides/redis-reference
@ApplicationScoped
public class ShortenedUrlCache {
    private final ValueCommands<String, String> api;

    public ShortenedUrlCache(RedisDataSource ds) {
        this.api = ds.value(String.class);
    }

    public void onStart(@Observes StartupEvent event) {
        System.out.println("Starting cache!");
        System.out.println("Setup cache!");
        this.api.set("shortened_url:1", "example");
        System.out.println("Get from cache!");
        System.out.println(this.api.get("shortened_url:1"));
    }
}
