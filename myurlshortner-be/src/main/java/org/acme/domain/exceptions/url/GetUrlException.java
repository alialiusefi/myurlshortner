package org.acme.domain.exceptions.url;

public abstract class GetUrlException extends Exception {
    public static class ShortenedUrlIsNotFound extends GetUrlException {

    }
}
