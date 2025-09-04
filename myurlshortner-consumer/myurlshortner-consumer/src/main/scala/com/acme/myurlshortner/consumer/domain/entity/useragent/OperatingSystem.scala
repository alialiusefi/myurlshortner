package com.acme.myurlshortner.consumer.domain.entity.useragent

sealed trait OperatingSystem(
  val value: String,
  val userAgentValue: String | Nothing
)

case object Windows   extends OperatingSystem("Windows", "Windows")
case object Macintosh extends OperatingSystem("Macintosh", "Macintosh")
case object Linux     extends OperatingSystem("Linux", "Linux")
case object OtherOS   extends OperatingSystem("OtherOS", null)
