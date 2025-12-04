package com.acme.myurlshortner.consumer.domain.notification.entity

data class ShortenedUrlViewedNTimes(
    val unique_identifier: String,
    val views: Long
)
