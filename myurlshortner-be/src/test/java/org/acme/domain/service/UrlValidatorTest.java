package org.acme.domain.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

public class UrlValidatorTest {

    @Test
    public void shouldReturnErrorForInvalidUrls() {
        List<String> testCases = List.of(
                "://www.google.com",
                "ftp://www.google.com",
                "http://",
                "www.google.com://urlpath://",
                "www.google.com://",
                ""
        );
        for (String testCase : testCases) {
            var either = UrlValidator.validateUrl("localhost", testCase);
            assertThat("Should have error", either.isLeft());
        }
    }

    @Test
    public void shouldAcceptValidUrls() {
        List<String> testCases = List.of(
                "subdomain.domain.com",
                "www.google.com",
                "http://www.google.com",
                "https://www.google.com",
                "google.com"
        );
        for (String testCase : testCases) {
            var either = UrlValidator.validateUrl("localhost", testCase);
            assertThat("Should not have error", either.isRight());
        }
    }
}
