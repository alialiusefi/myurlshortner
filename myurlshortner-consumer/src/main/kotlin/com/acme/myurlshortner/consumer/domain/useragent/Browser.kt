package com.acme.myurlshortner.consumer.domain.useragent

sealed class Browser {
    val userAgentValue: String?

    constructor(userAgentValue: String?) {
        this.userAgentValue = userAgentValue
    }

    data object Firefox : Browser("Firefox")
    data object Safari : Browser("Version")
    data object Chrome : Browser("Chrome")
    data object Other : Browser(null)
}
