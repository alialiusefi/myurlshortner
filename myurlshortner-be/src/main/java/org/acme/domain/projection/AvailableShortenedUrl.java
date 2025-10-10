package org.acme.domain.projection;

import java.net.URI;
import java.time.OffsetDateTime;

public record AvailableShortenedUrl(
        String uniqueIdentifier,
        URI originalUrl,
        OffsetDateTime createdAt,
        Long accessCount,
        Boolean isEnabled
) {
    public String shortenedUrl(String serviceHostname) {
        String format = "http://%s/goto/%s";
        return String.format(format, serviceHostname, this.uniqueIdentifier);
    }
}
