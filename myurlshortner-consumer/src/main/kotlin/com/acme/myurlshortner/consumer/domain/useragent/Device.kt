package com.acme.myurlshortner.consumer.domain.useragent

sealed class Device {
    val userAgentValue: String?

    constructor(userAgentValue: String?) {
        this.userAgentValue = userAgentValue
    }

    data object PC : Device("PC")
    data object Mac : Device("Mac")
    data object iPad : Device("iPad")
    data object iPhone : Device("iPhone")
    data object Android : Device("Android")
    data object Other : Device(null)
}
