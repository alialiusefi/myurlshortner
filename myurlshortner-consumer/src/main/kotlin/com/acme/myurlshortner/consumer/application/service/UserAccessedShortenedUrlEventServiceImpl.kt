package com.acme.myurlshortner.consumer.application.service

import com.acme.myurlshortner.consumer.application.util.TestErrorGenerator
import com.acme.myurlshortner.consumer.domain.useragent.Browser
import com.acme.myurlshortner.consumer.domain.useragent.Browser.*
import com.acme.myurlshortner.consumer.domain.useragent.Device
import com.acme.myurlshortner.consumer.domain.useragent.Device.*
import com.acme.myurlshortner.consumer.domain.useragent.OperatingSystem
import com.acme.myurlshortner.consumer.domain.useragent.OperatingSystem.Macintosh
import com.acme.myurlshortner.consumer.domain.useragent.OperatingSystem.Windows
import com.acme.myurlshortner.consumer.domain.userevent.command.UserAccessedShortenedUrlCommand
import com.acme.myurlshortner.consumer.domain.userevent.entity.UserAccessedShortenedUrl
import com.acme.myurlshortner.consumer.domain.userevent.repo.UserAccessedShortenedUrlRepo
import com.acme.myurlshortner.consumer.domain.userevent.service.UserAccessedShortenedUrlEventService
import org.springframework.stereotype.Service

@Service
class UserAccessedShortenedUrlEventServiceImpl(
    private val repo: UserAccessedShortenedUrlRepo,
) : UserAccessedShortenedUrlEventService {

    private val MOZILLA_PREFIX = "Mozilla/5.0"

    override fun handleShortenedUrlUserAccessed(
        command: UserAccessedShortenedUrlCommand,
    ) {
        // for demonstration purposes.
        TestErrorGenerator.generateTestError()
        val (device, browser, os) = if (command.userAgent.startsWith(MOZILLA_PREFIX)) {
            val noPrefix = command.userAgent.substring(MOZILLA_PREFIX.length + 1)
            val systemInfoIdxEnd = noPrefix.indexOfFirst { it == ')' } + 1
            val (device, os) = parseSystemInfo(noPrefix.take(systemInfoIdxEnd))
            val browser = when {
                command.userAgent.contains(Safari.userAgentValue!!) -> Safari
                command.userAgent.contains(Chrome.userAgentValue!!) -> Chrome
                command.userAgent.contains(Firefox.userAgentValue!!) -> Firefox
                else -> Browser.Other
            }
            Triple(device, browser, os)
        } else {
            Triple(Device.Other, Browser.Other, OperatingSystem.Other)
        }
        repo.saveUserAccessedShortenedUrl(
            UserAccessedShortenedUrl(
                uniqueIdentifier = command.uniqueIdentifier,
                originalUrl = command.originalUrl,
                shortenedUrl = command.shortenedUrl,
                device = device,
                browser = browser,
                operatingSystem = os,
                accessedAt = command.accessedAt
            )
        )
    }

    /**
     * Includes '(' and ')' in the substring.
     */
    private fun parseSystemInfo(substring: String): Pair<Device, OperatingSystem> {
        val deviceEndIdx = substring.indexOfFirst { it == ';' }.let { if (it == -1) 1 else it }
        val deviceSubString = substring.substring(1, deviceEndIdx)
        val device = when {
            deviceSubString.contains("iPhone") -> iPhone
            deviceSubString.contains("iPad") -> iPad
            deviceSubString.contains("Windows") || deviceSubString.contains("Linux") -> PC
            deviceSubString.contains("Macintosh") -> Mac
            else -> Device.Other
        }
        val osSubstring = substring.substring(deviceEndIdx + 1, substring.length - 1)
        return when {
            osSubstring.contains(Windows.userAgentValue!!) -> {
                device to OperatingSystem.Windows
            }

            osSubstring.contains(Macintosh.userAgentValue!!) -> {
                device to OperatingSystem.Macintosh
            }

            osSubstring.contains("Android") -> {
                Device.Android to OperatingSystem.Linux
            }

            else -> device to OperatingSystem.Other
        }
    }
}
