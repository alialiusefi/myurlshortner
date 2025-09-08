package com.acme.myurlshortner.consumer.domain.useragent

sealed class OperatingSystem {
    val userAgentValue: String?

    constructor(userAgentValue: String?) {
        this.userAgentValue = userAgentValue
    }

    data object Windows : OperatingSystem("Windows")
    data object Macintosh : OperatingSystem("Mac")
    data object Linux : OperatingSystem("Linux")
    data object Other : OperatingSystem(null)
}
