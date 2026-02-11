package com.acme.myurlshortner.consumer.domain.notification.repo

import java.time.OffsetDateTime

interface NotificationRepository {

    suspend fun insertShortenedUrlViewedNTimesNotification(
        userId: Long,
        uid: String,
        viewCount: Long,
        createdAt: OffsetDateTime,
    )
}
