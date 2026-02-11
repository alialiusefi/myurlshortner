package org.acme.domain.entity;

import org.acme.domain.events.*;
import org.jspecify.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class ShortenedUrlFactory {
    private static ShortenedUrl createShortenedUrlFromEvents(
            @Nullable ShortenedUrl start,
            List<? extends ShortenedUrlEvent> events
    ) {
        ShortenedUrl state = start;
        for (ShortenedUrlEvent event : events) {
            switch (event) {
                case V2UserCreatedShortenedUrlEvent created -> {
                    state = new ShortenedUrl(
                            created.originalUrl().toString(),
                            created.uniqueIdentifier(),
                            created.createdAt(),
                            created.createdAt(),
                            true,
                            created.userId(),
                            created.title()
                    );
                }
                case V1UserUpdatedOriginalUrlEvent updated -> {
                    if (state != null) {
                        state.setOriginalUrl(updated.newOriginalUrl());
                        state.setUpdatedAt(updated.updatedAt());
                    }
                }
                case V2UserGiftedShortenedUrlEvent gifted -> {
                    if (state != null) {
                        state.giftShortenedUrl(gifted);
                    }
                }

                case V1UserUpdatedTitleEvent titleUpdated -> {
                    if (state != null) {
                        state.updateTitle(titleUpdated);
                    }
                }
            }
        }
        return state;
    }

    public static ShortenedUrl createShortenedUrl(
            Iterator<List<? extends ShortenedUrlEvent>> events,
            boolean latestIsEnabled
    ) {
        ShortenedUrl state = null;
        while (events.hasNext()) {
            List<? extends ShortenedUrlEvent> current = events.next();
            state = createShortenedUrlFromEvents(state, current);
        }
        if (state != null) {
            state.setIsEnabled(latestIsEnabled);
        }
        return state;
    }
}
