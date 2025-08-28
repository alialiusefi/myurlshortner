package org.acme.application.service.urlshortner;

import io.vavr.control.Either;
import jakarta.inject.Singleton;
import org.acme.domain.ShortenedUrl;
import org.acme.domain.exceptions.shortenurl.ShortenUrlError;
import org.acme.domain.exceptions.shortenurl.ShortenUrlValidationException;
import org.acme.domain.repo.SaveShortenedUrlError;
import org.acme.domain.repo.ShortenedUrlRepository;
import org.acme.domain.service.ShortenedUrlService;
import org.acme.domain.service.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Singleton
public class ShortenedUrlServiceImpl implements ShortenedUrlService {
    private final ShortenedUrlRepository repo;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    ShortenedUrlServiceImpl(ShortenedUrlRepository repo) {
        this.repo = repo;
    }

    private String generateUniqueIdentifier() {
        final int UNIQUE_IDENTIFIER_SIZE = 10;
        Random random = new Random();
        IntStream stream = random.ints(0, ASCIITable.VALID_ASCII_TABLE.length - 1);
        Character[] result = stream.mapToObj((gen) -> ASCIITable.VALID_ASCII_TABLE[gen])
                .limit(UNIQUE_IDENTIFIER_SIZE)
                .toArray(Character[]::new);
        StringBuilder builder = new StringBuilder();
        for (Character i : result) {
            builder.append(i);
        }
        return builder.toString();
    }

    @Override
    public Either<ShortenUrlError, ShortenedUrl> generateShortenedUrl(String originalUrl) throws SaveShortenedUrlError {
        List<ShortenUrlValidationException> errors = UrlValidator.validateUrl(originalUrl);
        if (!errors.isEmpty()) {
            return Either.left(new ShortenUrlError(errors));
        }
        String uniqueIdentifier = this.generateUniqueIdentifier();
        ShortenedUrl shortUrl = new ShortenedUrl(URI.create(originalUrl), uniqueIdentifier);
        repo.insertShortenedUrl(shortUrl);
        logger.debug("Successfully generated a short url!");
        return Either.right(shortUrl);
    }
}
