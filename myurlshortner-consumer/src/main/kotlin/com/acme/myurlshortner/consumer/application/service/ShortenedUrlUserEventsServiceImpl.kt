package com.acme.myurlshortner.consumer.application.service

import com.acme.myurlshortner.consumer.domain.useragent.Browser
import com.acme.myurlshortner.consumer.domain.useragent.Browser.*
import com.acme.myurlshortner.consumer.domain.useragent.Device
import com.acme.myurlshortner.consumer.domain.useragent.Device.*
import com.acme.myurlshortner.consumer.domain.useragent.OperatingSystem
import com.acme.myurlshortner.consumer.domain.useragent.OperatingSystem.*
import com.acme.myurlshortner.consumer.domain.userevent.command.UserAccessedShortenedUrlCommand
import com.acme.myurlshortner.consumer.domain.userevent.entity.UserAccessedShortenedUrl
import com.acme.myurlshortner.consumer.domain.userevent.repo.UserAccessedShortenedUrlRepo
import com.acme.myurlshortner.consumer.domain.userevent.service.ShortenedUrlUserEventsService
import org.jboss.logging.Logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Service
class ShortenedUrlUserEventsServiceImpl(
    private val repo: UserAccessedShortenedUrlRepo
) : ShortenedUrlUserEventsService {

    private val logger = Logger.getLogger(ShortenedUrlUserEventsServiceImpl::class.java)

    @Transactional
    override fun handleShortenedUrlUserAccessed(
        command: UserAccessedShortenedUrlCommand,
    ) {
        val randomError = Random.nextInt(1, 101)
        if (randomError in 1..50) {
            logger.info("Generated error number $randomError")
            throw IllegalArgumentException("Random error 1 <= $randomError <= 50")
        } else {
            logger.info("No Error")
        }
        repo.saveUserAccessedShortenedUrl(
            UserAccessedShortenedUrl(
                uniqueIdentifier = command.uniqueIdentifier,
                originalUrl = command.originalUrl,
                shortenedUrl = command.shortenedUrl,
                device = when {
                    command.userAgent.contains(iPhone.userAgentValue!!) -> iPhone
                    command.userAgent.contains(iPad.userAgentValue!!) -> iPad
                    command.userAgent.contains(Android.userAgentValue!!) -> Android
                    command.userAgent.contains(PC.userAgentValue!!) -> PC
                    command.userAgent.contains(Mac.userAgentValue!!) -> Mac
                    else -> Device.Other
                },
                browser = when {
                    command.userAgent.contains(Safari.userAgentValue!!) -> Safari
                    command.userAgent.contains(Chrome.userAgentValue!!) -> Chrome
                    command.userAgent.contains(Firefox.userAgentValue!!) -> Firefox
                    else -> Browser.Other
                },
                operatingSystem = when {
                    command.userAgent.contains(Windows.userAgentValue!!) -> Windows
                    command.userAgent.contains(Macintosh.userAgentValue!!) -> Macintosh
                    command.userAgent.contains(Linux.userAgentValue!!) -> Linux
                    else -> OperatingSystem.Other
                },
                accessedAt = command.accessedAt
            )
        )
    }
}
