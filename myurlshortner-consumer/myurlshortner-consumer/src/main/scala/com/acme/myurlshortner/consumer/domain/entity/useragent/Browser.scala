package com.acme.myurlshortner.consumer.domain.entity.useragent

sealed trait BrowserType(
  val value: String,
  val userAgentValue: String
) {}

object MozillaFirefox extends BrowserType("Mozilla Firefox", "Firefox") {}
object Chrome         extends BrowserType("Google Chrome", "Chrome")    {}
object Safari         extends BrowserType("Safari", "Safari")           {}
object OtherBrowser   extends BrowserType("Other", "")                  {}
