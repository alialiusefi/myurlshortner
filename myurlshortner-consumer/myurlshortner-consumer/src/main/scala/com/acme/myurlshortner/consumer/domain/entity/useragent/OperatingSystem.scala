package com.acme.myurlshortner.consumer.domain.entity.useragent


sealed trait OperatingSystem(
    val value: String
)

object Windows extends OperatingSystem("Windows")
object Macintosh extends OperatingSystem("Macintosh")
object Linux extends OperatingSystem("Linux")
object OtherOS extends OperatingSystem("")
