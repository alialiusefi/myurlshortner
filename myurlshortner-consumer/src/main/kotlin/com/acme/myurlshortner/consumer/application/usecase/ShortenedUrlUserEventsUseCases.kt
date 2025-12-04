package com.acme.myurlshortner.consumer.application.usecase

import com.acme.events.UserAccessedShortenedUrl
import com.acme.myurlshortner.consumer.domain.userevent.command.UserAccessedShortenedUrlCommand
import com.acme.myurlshortner.consumer.domain.userevent.service.UserAccessedShortenedUrlEventService
import org.springframework.stereotype.Component
import java.net.URI
import java.time.OffsetDateTime

@Component
class ShortenedUrlUserEventsUseCases(
    private val service: UserAccessedShortenedUrlEventService
) {

    fun handleUserAccessedShortenedUrl(event: UserAccessedShortenedUrl) {
        val command = UserAccessedShortenedUrlCommand(
            uniqueIdentifier = event.uniqueIdentifier,
            originalUrl = URI.create(event.originalUrl),
            shortenedUrl = URI.create(event.shortenedUrl),
            userAgent = event.userAgent,
            accessedAt = event.accessedAt?.let { OffsetDateTime.parse(it) } ?: OffsetDateTime.now()
        )
        service.handleShortenedUrlUserAccessed(command)
    }
}
