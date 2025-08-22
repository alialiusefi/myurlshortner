package org.acme.domain.service;

import org.acme.domain.exceptions.ShortenUrlException;

import java.util.ArrayList;
import java.util.List;

public class UrlValidator {
    private final static Integer MAX_URL_SIZE = 1000;
    private final static String HTTP_PREFIX = "http";
    private final static String HTTPS_PREFIX = "https";
    private final static String SEPARATOR = "://";
    private final static String REGEX = "(http(s)?:\\/\\/.)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";

    public static List<ShortenUrlException> validateUrl(String url) {
        if (url.isBlank()) {
            return List.of(new ShortenUrlException.UrlIsEmptyException());
        }
        var listOfErrors = new ArrayList<ShortenUrlException>();
        if (url.length() > MAX_URL_SIZE) {
            listOfErrors.add(new ShortenUrlException.UrlIsTooLongException(MAX_URL_SIZE, url.length()));
        }

        String removedHttp = url;
        if (url.contains(SEPARATOR)) {
            int indexOfSeparator = url.indexOf(SEPARATOR);
            String protocol = url.substring(0, indexOfSeparator);
            if (protocol.isBlank()) {
                listOfErrors.add(new ShortenUrlException.UrlIsNotHttpException(url));
            }
            if (protocol.equals(HTTPS_PREFIX) || protocol.equals(HTTP_PREFIX)) {
                removedHttp = url.substring(indexOfSeparator + SEPARATOR.length());
            } else {
                listOfErrors.add(new ShortenUrlException.UrlIsNotHttpException(url));
            }
        }

        if (removedHttp.isBlank()) {
            listOfErrors.add(new ShortenUrlException.UrlIsMissingHostNameException(url));
            return listOfErrors;
        }

        if (!removedHttp.matches(REGEX)) {
            listOfErrors.add(new ShortenUrlException.UrlFormatIsNotValid());
        }

        return listOfErrors;
    }
}
