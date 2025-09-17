package com.acme.myurlshortner.consumer.application.repo.table

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestampWithTimeZone

object UserAccessedShortenedUrlTable : Table("shortened_url_user_access") {
    val uniqueIdentifier = varchar("unique_identifier", 10)
    val shortenedUrl = text("shortened_url")
    val originalUrl = text("original_url")
    val browser = varchar("browser", 32)
    val operatingSystem = varchar("operating_system", 32)
    val device = varchar("device", 32)
    val accessedAt = timestampWithTimeZone("accessed_at")
}
