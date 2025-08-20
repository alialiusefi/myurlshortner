package org.acme.domain.service;

import org.acme.domain.exceptions.ShortenedUrlException;

import java.util.ArrayList;
import java.util.List;

public class ShortenedUrlValidator {
    private final static Integer MAX_URL_SIZE = 1000;
    private final static String HTTP_PREFIX = "http://";
    private final static String HTTPS_PREFIX = "https://";

    public static List<ShortenedUrlException> validateUrl(String url) {
        if (url.isBlank()) {
            return List.of(new ShortenedUrlException.UrlIsEmptyException());
        }
        var listOfErrors = new ArrayList<ShortenedUrlException>();
        if (url.length() > MAX_URL_SIZE) {
            listOfErrors.add(new ShortenedUrlException.UrlIsTooLongException(MAX_URL_SIZE, url.length()));
        }
        if (!url.startsWith(HTTP_PREFIX) && !url.startsWith(HTTPS_PREFIX)) {
            listOfErrors.add(new ShortenedUrlException.UrlIsNotHttpException(url));
            return listOfErrors;
        } else {
            String removedHttp;
            if (url.startsWith(HTTPS_PREFIX)) {
                removedHttp = url.substring(HTTPS_PREFIX.length());
            } else {
                removedHttp = url.substring(HTTP_PREFIX.length());
            }
            if (removedHttp.isBlank()) {
                listOfErrors.add(new ShortenedUrlException.UrlIsMissingHostNameException(url));
                return listOfErrors;
            }
        }

        return listOfErrors;
    }
}
