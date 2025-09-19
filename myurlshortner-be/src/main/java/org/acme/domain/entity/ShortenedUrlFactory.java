package org.acme.domain.entity;

import org.acme.domain.events.ShortenedUrlEvent;
import org.acme.domain.events.V4UserCreatedShortenedUrlEvent;

import java.util.List;

public class ShortenedUrlFactory {
    public static ShortenedUrl createShortenedUrlFromEvents(List<ShortenedUrlEvent> events) {
        ShortenedUrl state = null;
        for (ShortenedUrlEvent event : events) {
            switch (event) {
                case V4UserCreatedShortenedUrlEvent created -> {
                    state = new ShortenedUrl(created.originalUrl(), created.uniqueIdentifier());
                }
                // case V4UserUpdatedShortenedUrlEvent updated when state != null -> apply(state, updated)
            }
        }
        return state;
    }

//    public static ShortenedUrl createShortenedUrlFromEvents(
//            Iterator<List<ShortenedUrlEvent>> events
//    ) {
//
//    }
}
