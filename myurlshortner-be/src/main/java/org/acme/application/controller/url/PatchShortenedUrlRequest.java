package org.acme.application.controller.url;

import org.acme.application.util.PatchField;

public record PatchShortenedUrlRequest(
        PatchField<String> url,
        PatchField<Boolean> isEnabled,
        PatchField<String> title
) {
    public static String URL_FIELD = "url";
    public static String IS_ENABLED_FIELD = "is_enabled";
    public static String TITLE_FIELD = "title";
}
