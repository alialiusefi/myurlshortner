package org.acme.domain.service;

import io.vavr.control.Either;
import org.acme.domain.exceptions.url.UrlValidationException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class UrlValidator {
    private final static Integer MAX_URL_SIZE = 1000;
    private final static String HTTP_PREFIX = "http";
    private final static String HTTPS_PREFIX = "https";
    private final static String SEPARATOR = "://";
    private final static String REGEX = "(http(s)?:\\/\\/.)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";

    public static Either<List<UrlValidationException>, URI> validateUrl(String hostname, String url) {
        if (url.isBlank()) {
            return Either.left(List.of(new UrlValidationException.UrlIsEmptyException()));
        }
        if (url.startsWith(".")) {
            return Either.left(List.of(new UrlValidationException.UrlFormatIsNotValid()));
        }
        var listOfErrors = new ArrayList<UrlValidationException>();
        if (url.length() > MAX_URL_SIZE) {
            listOfErrors.add(new UrlValidationException.UrlIsTooLongException(MAX_URL_SIZE, url.length()));
        }

        String removedHttp = url;
        if (url.contains(SEPARATOR)) {
            int indexOfSeparator = url.indexOf(SEPARATOR);
            String protocol = url.substring(0, indexOfSeparator);
            if (protocol.isBlank()) {
                listOfErrors.add(new UrlValidationException.UrlIsNotHttpException(url));
            }
            if (protocol.equals(HTTPS_PREFIX) || protocol.equals(HTTP_PREFIX)) {
                removedHttp = url.substring(indexOfSeparator + SEPARATOR.length());
            } else {
                listOfErrors.add(new UrlValidationException.UrlIsNotHttpException(url));
            }
        }

        if (removedHttp.isBlank()) {
            listOfErrors.add(new UrlValidationException.UrlIsMissingHostNameException(url));
            return Either.left(listOfErrors);
        }

        if (!removedHttp.matches(REGEX)) {
            listOfErrors.add(new UrlValidationException.UrlFormatIsNotValid());
        }

        URI originalUrl = URI.create(patchOriginalUrlWithHttps(url));
        if (originalUrl.getHost().equals(hostname) && originalUrl.getPath().startsWith("/goto")) {
            listOfErrors.add(new UrlValidationException.UrlFormatIsNotValid());
        }

        if (!listOfErrors.isEmpty()) {
            return Either.left(listOfErrors);
        } else {
            return Either.right(originalUrl);
        }
    }

    private static String patchOriginalUrlWithHttps(String originalUrl) {
        if (!originalUrl.startsWith(HTTP_PREFIX) && !originalUrl.startsWith(HTTPS_PREFIX)) {
            if (!originalUrl.startsWith("www.")) {
                return "https://www." + originalUrl;
            } else {
                return "https://" + originalUrl;
            }
        } else {
            return originalUrl;
        }
    }
}
