package com.acme.myurlshortner.consumer.domain.entity.userevent

import com.acme.myurlshortner.consumer.domain.entity.useragent.BrowserType
import com.acme.myurlshortner.consumer.domain.entity.useragent.Device
import com.acme.myurlshortner.consumer.domain.entity.useragent.OperatingSystem

import java.net.URI

// Data Type with has-a relationship
case class ShortenedUrlUserAccess(
  val browser: BrowserType,
  val device: Device,
  val os: OperatingSystem,
  val shortenedUrl: URI,
  val originalUrl: URI
)
