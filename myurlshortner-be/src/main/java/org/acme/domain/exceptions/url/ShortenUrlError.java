package org.acme.domain.exceptions.url;

import java.util.List;

public record ShortenUrlError(List<ShortenUrlValidationException> errors) {
}
