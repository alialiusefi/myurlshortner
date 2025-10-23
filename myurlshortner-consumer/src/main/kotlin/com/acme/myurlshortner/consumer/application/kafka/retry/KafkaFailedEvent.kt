package com.acme.myurlshortner.consumer.application.kafka.retry

data class KafkaFailedEvent(
    val id: Long,
    val eventType: KafkaEventType,
    val version: Int,
    val event: String,
    val retryCount: Int
)

enum class KafkaEventType {
    USER_ACCESSED_SHORTENED_URL
}
