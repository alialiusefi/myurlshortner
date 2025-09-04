package com.acme.myurlshortner.consumer.domain.entity.userevent

import com.acme.myurlshortner.consumer.domain.entity.useragent.BrowserType
import java.net.URI
import com.acme.myurlshortner.consumer.domain.entity.useragent.OperatingSystem
import com.acme.myurlshortner.consumer.domain.entity.useragent.Device

// Data Type with has-a relationship
case class ShortenedUrlUserAccess(
  val browser: BrowserType,
  val device: Device,
  val os: OperatingSystem,
  val shortenedUrl: URI,
  val originalUrl: URI
)
