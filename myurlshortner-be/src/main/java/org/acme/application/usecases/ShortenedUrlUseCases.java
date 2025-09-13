package org.acme.application.usecases;

import io.vavr.Tuple2;
import io.vavr.control.Either;
import jakarta.inject.Singleton;
import org.acme.application.exception.ApplicationException;
import org.acme.application.exception.OrderParamIsNotCorrectException;
import org.acme.application.exception.PageNumberIsNotCorrectException;
import org.acme.application.exception.PageSizeIsNotCorrectException;
import org.acme.application.exception.url.GetAvailableUrlsError;
import org.acme.domain.ShortenedUrl;
import org.acme.domain.exceptions.url.ShortenUrlError;
import org.acme.domain.query.AvailableShortenedUrlWithAccessCount;
import org.acme.domain.repo.SaveShortenedUrlError;
import org.acme.domain.service.ShortenedUrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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

    public Either<GetAvailableUrlsError, Tuple2<Long, List<AvailableShortenedUrlWithAccessCount>>> listAvailableUrls(Integer page, Integer size, String order) {
        List<ApplicationException> errors = new ArrayList<>();
        if (page == null || page < 1) {
            errors.add(new PageNumberIsNotCorrectException(page));
        }
        if (size == null || size < 1 || size > 100) {
            errors.add(new PageSizeIsNotCorrectException(size));
        }

        if (order != null) {
            var lowercase = order.toLowerCase();
            if (!lowercase.equals("desc") && !lowercase.equals("asc")) {
                errors.add(new OrderParamIsNotCorrectException(lowercase));
            }
        } else {
            order = "desc";
        }

        if (errors.isEmpty()) {
            return Either.right(service.listOfAvailableUrls(page, size, order.equals("asc")));
        } else {
            return Either.left(new GetAvailableUrlsError(errors));
        }
    }
}
