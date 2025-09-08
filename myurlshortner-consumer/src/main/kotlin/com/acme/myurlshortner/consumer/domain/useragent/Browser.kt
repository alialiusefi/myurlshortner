package com.acme.myurlshortner.consumer.domain.useragent

sealed class Browser {
    val value: String
    val userAgentValue: String?

    constructor(value: String, userAgentValue: String?) {
        this.value = value
        this.userAgentValue = userAgentValue
    }

    data object Firefox : Browser("Mozilla Firefox", "Firefox")
    data object Safari : Browser("Safari", "Safari")
    data object Chrome : Browser("Google Chrome", "Chrome")
    data object Other : Browser("Other", null)
}
