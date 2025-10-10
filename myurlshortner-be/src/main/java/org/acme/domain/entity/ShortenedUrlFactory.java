package org.acme.domain.entity;

import org.acme.domain.events.ShortenedUrlEvent;
import org.acme.domain.events.V1UserCreatedShortenedUrlEvent;
import org.acme.domain.events.V1UserUpdatedOriginalUrlEvent;

import java.util.List;

public class ShortenedUrlFactory {
    public static ShortenedUrl createShortenedUrlFromEvents(List<ShortenedUrlEvent> events) {
        ShortenedUrl state = null;
        for (ShortenedUrlEvent event : events) {
            switch (event) {
                case V1UserCreatedShortenedUrlEvent created -> {
                    state = new ShortenedUrl(created.originalUrl(), created.uniqueIdentifier());
                }
                case V1UserUpdatedOriginalUrlEvent updated -> {
                    if (state != null) {
                        state.setOriginalUrl(updated.newOriginalUrl());
                    }
                }
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
