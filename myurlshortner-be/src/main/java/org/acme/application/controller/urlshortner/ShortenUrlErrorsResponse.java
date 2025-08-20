package org.acme.application.controller.urlshortner;

import java.util.List;

public record ShortenUrlErrorsResponse(
        List<ShortenUrlError> errors
) {}
