package org.acme.domain.service;

import io.vavr.control.Either;
import org.acme.domain.ShortenedUrl;
import org.acme.domain.exceptions.ShortenUrlValidationError;
import org.acme.domain.repo.SaveShortenedUrlError;

public interface ShortenedUrlService {
    Either<ShortenUrlValidationError, ShortenedUrl> generateShortenedUrl(String originalUrl) throws SaveShortenedUrlError;
}
