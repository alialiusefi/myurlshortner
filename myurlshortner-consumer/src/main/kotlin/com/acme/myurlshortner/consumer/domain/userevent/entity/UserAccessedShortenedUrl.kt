package com.acme.myurlshortner.consumer.domain.userevent.entity

import com.acme.myurlshortner.consumer.domain.useragent.Browser
import com.acme.myurlshortner.consumer.domain.useragent.Device
import com.acme.myurlshortner.consumer.domain.useragent.OperatingSystem
import java.net.URI
import java.time.OffsetDateTime

data class UserAccessedShortenedUrl(
    val uniqueIdentifier: String,
    val originalUrl: URI,
    val shortenedUrl: URI,
    val device: Device,
    val browser: Browser,
    val operatingSystem: OperatingSystem,
    val accessedAt: OffsetDateTime,
)
