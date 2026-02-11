package com.acme.myurlshortner.consumer.application.kafka.consumer

import com.acme.events.ShortenedUrlUserEvents
import com.acme.myurlshortner.consumer.application.kafka.retry.ShortenedUrlUserEventsRetry
import com.acme.myurlshortner.consumer.application.usecase.ShortenedUrlUserEventsUseCases
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

@Component
class ShortenedUrlUserEventsConsumer(
    private val useCases: ShortenedUrlUserEventsUseCases,
    private val retry: ShortenedUrlUserEventsRetry
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Value($$"${app.podname}")
    lateinit var podName: String

    @KafkaListener(
        topics = [$$"${app.kafka.topic-name}"],
        autoStartup = $$"${app.kafka.enabled}"
    )
    suspend fun consume(message: ConsumerRecord<String, ShortenedUrlUserEvents>) {
        val key = message.key()
        val record = message.value()
        val datetime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(message.timestamp()), ZoneId.systemDefault())
        logger.info("Received message: key={} appendTime={} podName={}", key, datetime, podName)
        when {
            record.userAccessedShortenedUrlEvent != null -> try {
                // todo: handle ordering when same key is already in retry queue.
                useCases.handleUserAccessedShortenedUrl(record.userAccessedShortenedUrlEvent)
            } catch (e: Throwable) {
                logger.error("Failed to process message: $e", e)
                retry.handleFailedKafkaUserAccessedShortenedUrlEvent(record.userAccessedShortenedUrlEvent)
            }
            record.userCreatedShortenedUrlEvent != null -> try {
                useCases.handleUserCreatedShortenedUrl(record.userCreatedShortenedUrlEvent)
            } catch (e: Throwable) {
                logger.error("Failed to process message: $e", e)
                retry.handleFailedKafkaUserCreatedShortenedUrlEvent(record.userCreatedShortenedUrlEvent)
            }
        }
    }
}
