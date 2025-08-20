package org.acme.domain.service;

import io.vavr.control.Either;
import org.acme.domain.ShortenedUrl;
import org.acme.domain.exceptions.ShortenedUrlValidationError;
import org.acme.domain.repo.SaveShortenedUrlError;

public interface ShortenedUrlService {
    Either<ShortenedUrlValidationError, ShortenedUrl> generateShortenedUrl(String originalUrl) throws SaveShortenedUrlError;
}
