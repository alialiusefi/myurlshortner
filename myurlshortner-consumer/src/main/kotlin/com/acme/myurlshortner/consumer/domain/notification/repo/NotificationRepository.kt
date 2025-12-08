package com.acme.myurlshortner.consumer.domain.notification.repo

import java.time.OffsetDateTime

interface NotificationRepository {

    fun insertShortenedUrlViewedNTimesNotification(
        userId: Long,
        uid: String,
        viewCount: Long,
        createdAt: OffsetDateTime,
    )
}
