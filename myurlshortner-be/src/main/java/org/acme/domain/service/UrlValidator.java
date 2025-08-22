package org.acme.domain.service;

import org.acme.domain.exceptions.ShortenedUrlException;

import java.util.ArrayList;
import java.util.List;

public class UrlValidator {
    private final static Integer MAX_URL_SIZE = 1000;
    private final static String HTTP_PREFIX = "http";
    private final static String HTTPS_PREFIX = "https";
    private final static String SEPARATOR = "://";

    public static List<ShortenedUrlException> validateUrl(String url) {
        if (url.isBlank()) {
            return List.of(new ShortenedUrlException.UrlIsEmptyException());
        }
        var listOfErrors = new ArrayList<ShortenedUrlException>();
        if (url.length() > MAX_URL_SIZE) {
            listOfErrors.add(new ShortenedUrlException.UrlIsTooLongException(MAX_URL_SIZE, url.length()));
        }

        String removedHttp = url;
        if (url.contains(SEPARATOR)) {
            int indexOfSeparator = url.indexOf(SEPARATOR);
            String protocol = url.substring(0, indexOfSeparator);
            if (protocol.isBlank()) {
                listOfErrors.add(new ShortenedUrlException.UrlIsNotHttpException(url));
            }
            if (protocol.equals(HTTPS_PREFIX) || protocol.equals(HTTP_PREFIX)) {
                removedHttp = url.substring(indexOfSeparator + SEPARATOR.length());
            } else {
                listOfErrors.add(new ShortenedUrlException.UrlIsNotHttpException(url));
            }
        }

        if (removedHttp.isBlank()) {
            listOfErrors.add(new ShortenedUrlException.UrlIsMissingHostNameException(url));
            return listOfErrors;
        }

        return listOfErrors;
    }
}
