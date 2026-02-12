package com.acme.myurlshortner.consumer.application.repo

import com.acme.myurlshortner.consumer.application.kafka.retry.KafkaEventType
import com.acme.myurlshortner.consumer.application.kafka.retry.KafkaFailedEvent
import com.acme.myurlshortner.consumer.application.repo.table.KafkaRetryKeysTable
import com.acme.myurlshortner.consumer.application.repo.table.KafkaRetryQueueTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.core.vendors.ForUpdateOption
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
class KafkaRetryQueueRepository {

    enum class KafkaRetryKeyStatus {
        AVAILABLE,
        BUSY
    }

    suspend fun insertFailedEvent(
        event: String,
        eventDateTime: OffsetDateTime,
        key: String,
        type: KafkaEventType,
        version: Int,
        topic: String
    ) = newSuspendedTransaction(Dispatchers.IO) {
        KafkaRetryKeysTable.insertIgnore {
            it[this.key] = key
            it[this.topic] = topic
            it[this.status] = KafkaRetryKeyStatus.AVAILABLE
        }
        KafkaRetryQueueTable.insert {
            it[this.event] = event
            it[this.eventDateTime] = eventDateTime
            it[this.key] = key
            it[this.eventType] = type
            it[this.version] = version
            it[this.topic] = topic
            it[this.retryCount] = 0
        }
        return@newSuspendedTransaction
    }


    suspend fun fetchEarliestFailedEventAndLockKey(topic: String): KafkaFailedEvent? =
        newSuspendedTransaction(Dispatchers.IO) {
            val event = KafkaRetryKeysTable.join(
                KafkaRetryQueueTable,
                onColumn = null,
                otherColumn = null,
                joinType = JoinType.INNER,
                additionalConstraint = {
                    KafkaRetryKeysTable.key.eq(KafkaRetryQueueTable.key)
                        .and(KafkaRetryKeysTable.topic.eq(KafkaRetryQueueTable.topic))
                        .and(KafkaRetryQueueTable.retryCount.less(3))
                        .and(KafkaRetryKeysTable.topic.eq(topic))
                        .and(KafkaRetryKeysTable.status.eq(KafkaRetryKeyStatus.AVAILABLE))
                }
            ).select(
                KafkaRetryQueueTable.id,
                KafkaRetryKeysTable.key,
                KafkaRetryQueueTable.retryCount,
                KafkaRetryQueueTable.event,
                KafkaRetryQueueTable.eventType,
                KafkaRetryQueueTable.version,
                KafkaRetryQueueTable.topic
            ).orderBy(KafkaRetryQueueTable.eventDateTime)
                .limit(1)
                .forUpdate(
                    ForUpdateOption.PostgreSQL.ForUpdate()
                ).map {
                    KafkaFailedEvent(
                        id = it[KafkaRetryQueueTable.id],
                        key = it[KafkaRetryKeysTable.key],
                        eventType = it[KafkaRetryQueueTable.eventType],
                        version = it[KafkaRetryQueueTable.version],
                        event = it[KafkaRetryQueueTable.event],
                        retryCount = it[KafkaRetryQueueTable.retryCount],
                        topic = it[KafkaRetryQueueTable.topic]
                    )
                }.firstOrNull() ?: return@newSuspendedTransaction null

            KafkaRetryKeysTable.update(limit = null, where = {
                KafkaRetryKeysTable.key.eq(event.key).and(
                    KafkaRetryKeysTable.topic.eq(event.topic)
                )
            }) {
                it[KafkaRetryKeysTable.status] = KafkaRetryKeyStatus.BUSY
            }
            return@newSuspendedTransaction event
        }


    suspend fun deleteFailedEventFromQueue(id: Long) = newSuspendedTransaction(Dispatchers.IO) {
        KafkaRetryQueueTable.deleteWhere {
            KafkaRetryQueueTable.id.eq(id)
        }
    }


    suspend fun updateRetryCountOfFailedEvent(id: Long, setRetryCount: Int) = newSuspendedTransaction(Dispatchers.IO) {
        KafkaRetryQueueTable.update(limit = null, where = { KafkaRetryQueueTable.id.eq(id) }) {
            it[KafkaRetryQueueTable.retryCount] = setRetryCount
        }
    }


    suspend fun unlockKey(key: String, topic: String) = newSuspendedTransaction(Dispatchers.IO) {
        KafkaRetryKeysTable.update(limit = null, where = {
            KafkaRetryKeysTable.key.eq(key).and(
                KafkaRetryKeysTable.topic.eq(topic)
            )
        }) {
            it[KafkaRetryKeysTable.status] = KafkaRetryKeyStatus.AVAILABLE
        }
    }
}
