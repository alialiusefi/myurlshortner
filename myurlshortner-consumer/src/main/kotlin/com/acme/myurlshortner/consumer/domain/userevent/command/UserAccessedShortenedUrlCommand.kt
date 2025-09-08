package com.acme.myurlshortner.consumer.domain.userevent.command

import java.net.URI
import java.time.OffsetDateTime

data class UserAccessedShortenedUrlCommand(
    val originalUrl: URI,
    val shortenedUrl: URI,
    val userAgent: String,
    val accessedAt: OffsetDateTime,
)
