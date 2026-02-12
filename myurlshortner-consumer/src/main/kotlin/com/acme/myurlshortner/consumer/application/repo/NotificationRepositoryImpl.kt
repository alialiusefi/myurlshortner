package com.acme.myurlshortner.consumer.application.repo

import com.acme.myurlshortner.consumer.application.repo.table.NotificationTable
import com.acme.myurlshortner.consumer.domain.notification.entity.ShortenedUrlViewedNTimes
import com.acme.myurlshortner.consumer.domain.notification.repo.NotificationRepository
import com.acme.myurlshortner.consumer.domain.notification.repo.NotificationType
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
class NotificationRepositoryImpl : NotificationRepository {

    private val objectMapper = ObjectMapper()

    
    override suspend fun insertShortenedUrlViewedNTimesNotification(
        userId: Long,
        uid: String,
        viewCount: Long,
        createdAt: OffsetDateTime,
    ) = newSuspendedTransaction(Dispatchers.IO) {
        val paramsJson = objectMapper.writeValueAsString(ShortenedUrlViewedNTimes(uid, viewCount))
        NotificationTable.insert {
            it[NotificationTable.userId] = userId
            it[NotificationTable.type] = NotificationType.SHORTENED_URL_REACHED_N_VIEWS.toString()
            it[NotificationTable.uniqueIdentifier] = uid
            it[NotificationTable.params] = paramsJson
            it[NotificationTable.createdAt] = createdAt
            it[NotificationTable.readAt] = null
        }
        return@newSuspendedTransaction
    }
}
