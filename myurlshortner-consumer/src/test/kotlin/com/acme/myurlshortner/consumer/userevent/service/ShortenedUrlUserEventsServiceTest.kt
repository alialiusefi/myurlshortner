package com.acme.myurlshortner.consumer.userevent.service

import com.acme.myurlshortner.consumer.application.service.UserAccessedShortenedUrlEventServiceImpl
import com.acme.myurlshortner.consumer.domain.useragent.Browser
import com.acme.myurlshortner.consumer.domain.useragent.Device
import com.acme.myurlshortner.consumer.domain.useragent.OperatingSystem
import com.acme.myurlshortner.consumer.domain.userevent.command.UserAccessedShortenedUrlCommand
import com.acme.myurlshortner.consumer.domain.userevent.entity.UserAccessedShortenedUrl
import com.acme.myurlshortner.consumer.domain.userevent.repo.UserAccessedShortenedUrlRepo
import io.mockk.mockk
import io.mockk.verify
import java.net.URI
import java.time.OffsetDateTime
import kotlin.test.Test

class ShortenedUrlUserEventsServiceTest {
    val mockRepo = mockk<UserAccessedShortenedUrlRepo>(relaxed = true);
    val listOfNormalUserAgents = mapOf(
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                to Triple(Device.PC, OperatingSystem.Windows, Browser.Chrome),
        "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Mobile Safari/537.36"
                to Triple(Device.Android, OperatingSystem.Linux, Browser.Chrome),
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36"
                to Triple(Device.Mac, OperatingSystem.Macintosh, Browser.Chrome),
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.5 Safari/605.1.15"
                to Triple(Device.Mac, OperatingSystem.Macintosh, Browser.Safari),
        "Mozilla/5.0 (iPhone; CPU iPhone OS 18_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/26.0 Mobile/15E148 Safari/604.1"
                to Triple(Device.iPhone, OperatingSystem.Macintosh, Browser.Safari)
    )
    val listOfBotCrawlersUserAgents = mapOf(
        "Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5X Build/MMB29P) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/W.X.Y.Z Mobile Safari/537.36 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)"
                to Triple(Device.Android, OperatingSystem.Linux, Browser.Chrome),
        "Googlebot/2.1 (+http://www.google.com/bot.html)" to Triple(Device.Other, OperatingSystem.Other, Browser.Other),
        "Googlebot-Image/1.0" to Triple(Device.Other, OperatingSystem.Other, Browser.Other),
        "Googlebot-Video/1.0" to Triple(Device.Other, OperatingSystem.Other, Browser.Other),
    )

    val listOfLibraryLikeUserAgents = mapOf(
        "curl/7.64.1" to Triple(Device.Other, OperatingSystem.Other, Browser.Other),
        "PostmanRuntime/7.26.5" to Triple(Device.Other, OperatingSystem.Other, Browser.Other),
    )

    @Test
    fun shouldSaveUserAccessEvent() {
        val service = UserAccessedShortenedUrlEventServiceImpl(mockRepo)
        val uid = "abcabcabc1"
        val originalUrl = URI.create("https://www.example.com")
        val shortenedUrl = URI.create("http://localhost/goto${uid}")
        val accessedAt = OffsetDateTime.now()
        (listOfNormalUserAgents + listOfBotCrawlersUserAgents + listOfLibraryLikeUserAgents).map {
            service.handleShortenedUrlUserAccessed(
                UserAccessedShortenedUrlCommand(
                    uniqueIdentifier = uid,
                    originalUrl = originalUrl,
                    shortenedUrl = shortenedUrl,
                    userAgent = it.key,
                    accessedAt = accessedAt
                )
            )
            verify {
                mockRepo.saveUserAccessedShortenedUrl(
                    access = UserAccessedShortenedUrl(
                        uniqueIdentifier = uid,
                        originalUrl = originalUrl,
                        shortenedUrl = shortenedUrl,
                        device = it.value.first,
                        browser = it.value.third,
                        operatingSystem = it.value.second,
                        accessedAt = accessedAt
                    )
                )
            }
        }
    }
}
