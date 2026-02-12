package com.acme.myurlshortner.consumer.application.kafka.retry

data class KafkaFailedEvent(
    val id: Long,
    val key: String,
    val eventType: KafkaEventType,
    val version: Int,
    val event: String,
    val retryCount: Int,
    val topic: String
)

enum class KafkaEventType {
    USER_ACCESSED_SHORTENED_URL,
    USER_CREATED_SHORTENED_URL
}
