package com.acme.myurlshortner.consumer.application.repo.table

import com.acme.myurlshortner.consumer.application.repo.KafkaRetryQueueRepository.KafkaRetryKeyStatus
import org.jetbrains.exposed.v1.core.Table

object KafkaRetryKeysTable : Table("kafka_retry_keys") {
    val key = varchar("key", 32)
    val topic = varchar("topic", 64)
    val status = enumerationByName<KafkaRetryKeyStatus>("status", 32)
}
