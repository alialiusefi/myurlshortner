package com.acme.myurlshortner.consumer.domain.userevent.command

import java.net.URI
import java.time.OffsetDateTime

data class UserCreatedShortenedUrlCommand(
    val uniqueIdentifier: String,
    val originalUrl: URI,
    val userId: Long,
    val title: String?,
    val createdAt: OffsetDateTime,
)
