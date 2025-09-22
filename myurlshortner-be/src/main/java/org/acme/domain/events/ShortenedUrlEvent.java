package org.acme.domain.events;

public sealed interface ShortenedUrlEvent permits V4UserCreatedShortenedUrlEvent, V5UserUpdatedOriginalUrlEvent {

}
