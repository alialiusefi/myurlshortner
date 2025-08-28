package org.acme.domain.exceptions.shortenurl;

import java.util.List;

public record ShortenUrlError(List<ShortenUrlValidationException> errors) {}
