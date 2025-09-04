package com.acme.myurlshortner.consumer.domain.entity.useragent

// Data Type with is-a relationship
sealed abstract class BrowserType(
  val value: String,
  val userAgentValue: String | Nothing
)

case object MozillaFirefox extends BrowserType("Mozilla Firefox", "Firefox")
case object Chrome         extends BrowserType("Google Chrome", "Chrome")    
case object Safari         extends BrowserType("Safari", "Safari")           
case object OtherBrowser   extends BrowserType("Other", null)                 
