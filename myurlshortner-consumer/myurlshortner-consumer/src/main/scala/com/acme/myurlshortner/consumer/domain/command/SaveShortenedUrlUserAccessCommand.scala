package com.acme.myurlshortner.consumer.domain.command

import java.net.URI

case class SaveShortenedUrlUserAccessCommand(
  val userAgent: String, // User-Agent: Mozilla/5.0 (<system-information>) <platform> (<platform-details>) <extensions>
  val originalUrl: URI,
  val shortenedUrl: URI
)
