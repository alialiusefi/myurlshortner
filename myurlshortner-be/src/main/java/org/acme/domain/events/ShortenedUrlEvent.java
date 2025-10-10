package org.acme.domain.events;

public sealed interface ShortenedUrlEvent permits V1UserCreatedShortenedUrlEvent, V1UserUpdatedOriginalUrlEvent {

}
