package org.acme.domain.entity;

import org.acme.application.repo.eventstore.ShortenedUrlEventRepository;
import org.acme.domain.events.ShortenedUrlEventEnvelopFactory;
import org.acme.domain.events.V1UserUpdatedOriginalUrlEvent;
import org.acme.domain.events.V1UserUpdatedTitleEvent;
import org.acme.domain.events.V2UserCreatedShortenedUrlEvent;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ShortenedUrlFactoryTest {
    @Test
    void shouldReturnShortenedUrlWithSingleCreatedEvent() {
        var uid = "asdfghjkl1";
        var createdAt = OffsetDateTime.now();
        var shortenedUrl = new ShortenedUrl(
                URI.create("www.abc.com").toString(),
                uid,
                createdAt,
                createdAt,
                true,
                1L,
                "test"
        );
        List created = List.of(ShortenedUrlEventEnvelopFactory.createV2CreatedShortenUrlEvent(shortenedUrl).getEvent());
        var iterator = Mockito.mock(ShortenedUrlEventRepository.ShortenedUrlEventIterator.class); // the factory relies on the api of the iterator
        Mockito.when(iterator.hasNext()).thenReturn(true).thenReturn(false);
        Mockito.when(iterator.next()).thenReturn(created);
        var actualShortenedUrl = ShortenedUrlFactory.createShortenedUrl(iterator, true);

        assertThat(actualShortenedUrl, equalTo(shortenedUrl));
    }

    @Test
    void shouldReturnShortenedUrlFromCreatedEventAndMultipleUpdatedEvent() {
        var uid = "asdfghjkl1";
        var createdAt = OffsetDateTime.now();
        var shortenedUrl = new ShortenedUrl(
                URI.create("www.abc.com").toString(),
                uid,
                createdAt,
                createdAt,
                false,
                1L,
                "test"
        );
        V2UserCreatedShortenedUrlEvent created = ShortenedUrlEventEnvelopFactory.createV2CreatedShortenUrlEvent(shortenedUrl).getEvent();
        List events1 = List.of(created);

        V1UserUpdatedOriginalUrlEvent updated = ShortenedUrlEventEnvelopFactory.createV1UpdatedOriginalUrlEvent(shortenedUrl, URI.create("www.example.com")).getEvent();
        List events2 = List.of(updated);

        V1UserUpdatedOriginalUrlEvent updated2 = ShortenedUrlEventEnvelopFactory.createV1UpdatedOriginalUrlEvent(shortenedUrl, URI.create("www.example2.com")).getEvent();
        List events3 = List.of(updated2);

        V1UserUpdatedTitleEvent updated3 = ShortenedUrlEventEnvelopFactory.createV1UpdatedTitleEvent(shortenedUrl, "new").getEvent();
        List events4 = List.of(updated3);

        var iterator = Mockito.mock(ShortenedUrlEventRepository.ShortenedUrlEventIterator.class); // the factory relies on the api of the iterator
        Mockito.when(iterator.hasNext()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
        Mockito.when(iterator.next()).thenReturn(events1).thenReturn(events2).thenReturn(events3).thenReturn(events4);
        var actualShortenedUrl = ShortenedUrlFactory.createShortenedUrl(iterator, false);

        assertThat("new", Matchers.equalTo(actualShortenedUrl.getTitle()));
        assertThat(URI.create("www.example2.com"), Matchers.equalTo(actualShortenedUrl.getOriginalUrl()));
        assertThat(shortenedUrl.getUserId(), Matchers.equalTo(actualShortenedUrl.getUserId()));
        assertThat(shortenedUrl.getPublicIdentifier(), Matchers.equalTo(actualShortenedUrl.getPublicIdentifier()));
    }
}
