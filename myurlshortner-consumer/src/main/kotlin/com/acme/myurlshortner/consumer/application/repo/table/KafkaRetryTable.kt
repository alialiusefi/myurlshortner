package com.acme.myurlshortner.consumer.application.repo.table

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestampWithTimeZone
import org.jetbrains.exposed.v1.json.json

object KafkaRetryTable : Table("kafka_retry_table") {
    val id = long("id").autoIncrement()
    val key = varchar("key", 32)
    val eventType = varchar("event_type", 32)
    val eventDateTime = timestampWithTimeZone("event_date_time")
    val version = integer("version")
    val event = json("event", { a -> a }, { a -> a })
    val topic = varchar("topic", 64)
    val retryCount = integer("retry_count")
}
