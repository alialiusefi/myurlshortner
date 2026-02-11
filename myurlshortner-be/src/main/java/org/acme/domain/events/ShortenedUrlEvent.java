package org.acme.domain.events;

public sealed interface ShortenedUrlEvent permits V2UserCreatedShortenedUrlEvent, V2UserGiftedShortenedUrlEvent, V1UserUpdatedOriginalUrlEvent, V1UserUpdatedTitleEvent {

}
