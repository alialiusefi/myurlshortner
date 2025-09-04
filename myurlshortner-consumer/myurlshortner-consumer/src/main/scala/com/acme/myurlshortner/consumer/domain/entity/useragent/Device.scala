package com.acme.myurlshortner.consumer.domain.entity.useragent

sealed trait Device(
    val value: String,
    val userAgentValue: String | Nothing
)

case object PC      extends Device("PC", null)
case object Mac     extends Device("Macintosh", "CPU OS")
case object iPad    extends Device("iPad", "CPU iPad")
case object iPhone  extends Device("iPhone", "CPU iPhone")
case object Android extends Device("Android", "Android")
