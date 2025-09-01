package org.acme.application.usecases;

import io.vavr.control.Either;
import jakarta.inject.Singleton;
import org.acme.domain.ShortenedUrl;
import org.acme.domain.exceptions.url.ShortenUrlError;
import org.acme.domain.repo.SaveShortenedUrlError;
import org.acme.domain.service.ShortenedUrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ShortenedUrlUseCases {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ShortenedUrlService service;

    public ShortenedUrlUseCases(ShortenedUrlService service) {
        this.service = service;
    }

    public Either<ShortenUrlError, ShortenedUrl> generateShortenedUrl(String originalUrl) {
        for (int i = 0; i < 3; i++) {
            try {
                return service.generateShortenedUrl(originalUrl);
            } catch (SaveShortenedUrlError e) {
                logger.warn("Unable to generate url", e);
            }
        }
        var message = "Failed to generate url after all retry attempts";
        logger.error(message);
        throw new IllegalStateException(message);
    }
}
