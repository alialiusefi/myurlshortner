package com.acme.myurlshortner.consumer.application.repo

import com.acme.myurlshortner.consumer.application.kafka.retry.KafkaEventType
import com.acme.myurlshortner.consumer.application.kafka.retry.KafkaFailedEvent
import com.acme.myurlshortner.consumer.application.repo.table.KafkaRetryTable
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.core.vendors.ForUpdateOption
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.update
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Repository
class KafkaRetryQueueRepository {

    @Transactional
    fun insertFailedEvent(
        event: String,
        eventDateTime: OffsetDateTime,
        key: String,
        type: KafkaEventType,
        version: Int,
        topic: String
    ) {
        KafkaRetryTable.insert {
            it[this.event] = event
            it[this.eventDateTime] = eventDateTime
            it[this.key] = key
            it[this.eventType] = type.toString()
            it[this.version] = version
            it[this.topic] = topic
            it[this.retryCount] = 0
        }
    }

    @Transactional
    fun fetchLatestFailedEvent(topic: String): KafkaFailedEvent? {
        val rank = rank()
            .over()
            .partitionBy(KafkaRetryTable.key)
            .orderBy(KafkaRetryTable.eventDateTime)
            .alias("rnk")
        val pendingEvents = KafkaRetryTable.select(
            KafkaRetryTable.id,
            rank
        ).where(KafkaRetryTable.retryCount.less(3) and KafkaRetryTable.topic.eq(topic))
            .orderBy(KafkaRetryTable.eventDateTime).alias("temp")
        return KafkaRetryTable.join(
            pendingEvents,
            onColumn = KafkaRetryTable.id,
            otherColumn = pendingEvents[KafkaRetryTable.id],
            joinType = JoinType.INNER
        ).select(
            KafkaRetryTable.id,
            KafkaRetryTable.retryCount,
            KafkaRetryTable.event,
            KafkaRetryTable.eventType,
            KafkaRetryTable.version
        ).where(pendingEvents[rank].eq(1)).limit(1)
            .forUpdate(ForUpdateOption.PostgreSQL.ForUpdate(ForUpdateOption.PostgreSQL.MODE.SKIP_LOCKED)).map {
            KafkaFailedEvent(
                id = it[KafkaRetryTable.id],
                eventType = KafkaEventType.valueOf(it[KafkaRetryTable.eventType]),
                version = it[KafkaRetryTable.version],
                event = it[KafkaRetryTable.event],
                retryCount = it[KafkaRetryTable.retryCount]
            )
        }.firstOrNull()
    }

    @Transactional
    fun updatedRetryCount(id: Long, setRetryCount: Int) {
        KafkaRetryTable.update(
            limit = null,
            where = { KafkaRetryTable.id.eq(id) }
        ) {
            it[KafkaRetryTable.retryCount] = setRetryCount
        }
    }

    @Transactional
    fun deleteFailedEvent(id: Long) {
        KafkaRetryTable.deleteWhere { KafkaRetryTable.id.eq(id) }
    }
}
