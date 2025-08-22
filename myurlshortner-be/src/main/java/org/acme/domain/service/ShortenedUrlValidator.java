package org.acme.domain.service;

import org.acme.domain.exceptions.ShortenedUrlException;

import java.util.ArrayList;
import java.util.List;

public class ShortenedUrlValidator {
    private final static Integer MAX_URL_SIZE = 1000;
    private final static String HTTP_PREFIX = "http";
    private final static String HTTPS_PREFIX = "https";
    private final static String SEPARATOR_PREFIX = "://";

    public static List<ShortenedUrlException> validateUrl(String url) {
        if (url.isBlank()) {
            return List.of(new ShortenedUrlException.UrlIsEmptyException());
        }
        var listOfErrors = new ArrayList<ShortenedUrlException>();
        if (url.length() > MAX_URL_SIZE) {
            listOfErrors.add(new ShortenedUrlException.UrlIsTooLongException(MAX_URL_SIZE, url.length()));
        }

        String removedHttp = url;
        if (url.contains(SEPARATOR_PREFIX)) {
            String[] split = url.split(SEPARATOR_PREFIX);
            String protocol = split[0];
            if (!protocol.equals(HTTP_PREFIX)) {
                listOfErrors.add(new ShortenedUrlException.UrlIsNotHttpException(url));
            }
            if (!protocol.equals(HTTPS_PREFIX)) {
                listOfErrors.add(new ShortenedUrlException.UrlIsNotHttpException(url));
            }
            removedHttp = split[1];
        }

        if (removedHttp.isBlank()) {
            listOfErrors.add(new ShortenedUrlException.UrlIsMissingHostNameException(url));
            return listOfErrors;
        }

        return listOfErrors;
    }
}
