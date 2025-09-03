package com.acme.myurlshortner.consumer.domain.entity.useragent

sealed trait Device(val value: String, val userAgentValue: String)

object PC extends Device("", "")
object Mac extends Device("Macintosh", "CPU OS")
object iPad extends Device("iPad", "CPU iPad")
object iPhone extends Device("iPhone", "CPU iPhone")
object Android extends Device("Android", "Android")
