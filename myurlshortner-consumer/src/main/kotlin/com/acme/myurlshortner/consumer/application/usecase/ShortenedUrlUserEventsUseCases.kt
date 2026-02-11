package com.acme.myurlshortner.consumer.application.usecase

import com.acme.events.UserAccessedShortenedUrl
import com.acme.events.UserCreatedShortenedUrl
import com.acme.myurlshortner.consumer.domain.userevent.command.UserAccessedShortenedUrlCommand
import com.acme.myurlshortner.consumer.domain.userevent.command.UserCreatedShortenedUrlCommand
import com.acme.myurlshortner.consumer.domain.userevent.service.UserAccessedShortenedUrlEventService
import com.acme.myurlshortner.consumer.domain.userevent.service.UserCreatedShortenedUrlService
import org.springframework.stereotype.Component
import java.net.URI
import java.time.OffsetDateTime

@Component
class ShortenedUrlUserEventsUseCases(
    private val userAccessEventService: UserAccessedShortenedUrlEventService,
    private val userShortenedUrlService: UserCreatedShortenedUrlService
) {

    fun handleUserAccessedShortenedUrl(event: UserAccessedShortenedUrl) {
        val command = UserAccessedShortenedUrlCommand(
            uniqueIdentifier = event.uniqueIdentifier,
            originalUrl = URI.create(event.originalUrl),
            shortenedUrl = URI.create(event.shortenedUrl),
            userAgent = event.userAgent,
            accessedAt = event.accessedAt?.let { OffsetDateTime.parse(it) } ?: OffsetDateTime.now()
        )
        userAccessEventService.handleShortenedUrlUserAccessed(command)
    }

    fun handleUserCreatedShortenedUrl(event: UserCreatedShortenedUrl) {
        userShortenedUrlService.handleShortenedUrlCreated(
            UserCreatedShortenedUrlCommand(
                uniqueIdentifier = event.uniqueIdentifier,
                originalUrl = URI.create(event.originalUrl),
                userId = event.userId,
                title = event.title,
                createdAt = OffsetDateTime.parse(event.createdAt)
            )
        )
    }
}
