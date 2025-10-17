package org.acme.domain.entity;

import org.acme.application.repo.eventstore.ShortenedUrlEventRepository;
import org.acme.domain.events.ShortenedUrlEventEnvelopFactory;
import org.acme.domain.events.V1UserCreatedShortenedUrlEvent;
import org.acme.domain.events.V1UserUpdatedOriginalUrlEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UrlShortenerFactoryTest {


    @Test
    void shouldCreateUrlShortenerFromCreatedEvent() {
        var uid = "asdfghjkl1";
        var createdAt = OffsetDateTime.now();
        var shortenedUrl = new ShortenedUrl(
                URI.create("www.abc.com").toString(),
                uid,
                createdAt,
                createdAt,
                true
        );
        List created = List.of(ShortenedUrlEventEnvelopFactory.createV1CreatedShortenUrlEvent(shortenedUrl).getEvent());
        var iterator = Mockito.mock(ShortenedUrlEventRepository.ShortenedUrlEventIterator.class); // the factory relies on the api of the iterator
        Mockito.when(iterator.hasNext()).thenReturn(true).thenReturn(false);
        Mockito.when(iterator.next()).thenReturn(created);
        var actualShortenedUrl = ShortenedUrlFactory.createShortenedUrl(iterator, true);

        assertThat(actualShortenedUrl, equalTo(shortenedUrl));
    }

    @Test
    void shouldCreateUrlShortenerFromCreatedEventAndMultipleUpdatedEvent() {
        var uid = "asdfghjkl1";
        var createdAt = OffsetDateTime.now();
        var shortenedUrl = new ShortenedUrl(
                URI.create("www.abc.com").toString(),
                uid,
                createdAt,
                createdAt,
                false
        );
        V1UserCreatedShortenedUrlEvent created = ShortenedUrlEventEnvelopFactory.createV1CreatedShortenUrlEvent(shortenedUrl).getEvent();
        shortenedUrl.setOriginalUrl(URI.create("www.example.com"));
        V1UserUpdatedOriginalUrlEvent updated = ShortenedUrlEventEnvelopFactory.createV1UpdatedOriginalUrlEvent(shortenedUrl).getEvent();
        shortenedUrl.setOriginalUrl(URI.create("www.example2.com"));
        V1UserUpdatedOriginalUrlEvent updated2 = ShortenedUrlEventEnvelopFactory.createV1UpdatedOriginalUrlEvent(shortenedUrl).getEvent();

        List events = List.of(created, updated, updated2);
        var iterator = Mockito.mock(ShortenedUrlEventRepository.ShortenedUrlEventIterator.class); // the factory relies on the api of the iterator
        Mockito.when(iterator.hasNext()).thenReturn(true).thenReturn(false);
        Mockito.when(iterator.next()).thenReturn(events);
        var actualShortenedUrl = ShortenedUrlFactory.createShortenedUrl(iterator, false);

        assertThat(actualShortenedUrl, equalTo(shortenedUrl));
    }
}
