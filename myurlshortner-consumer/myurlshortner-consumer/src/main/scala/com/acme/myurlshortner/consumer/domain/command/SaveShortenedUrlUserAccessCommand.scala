package com.acme.myurlshortner.consumer.domain.command

import java.net.URI

case class SaveShortenedUrlUserAccessCommand(
    val userAgent: String,
    val originalUrl: URI,
    val shortenedUrl: URI
)
