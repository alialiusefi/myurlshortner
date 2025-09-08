package com.acme.myurlshortner.consumer.application.consumer

import com.acme.events.ShortenedUrlUserEvents
import com.acme.myurlshortner.consumer.application.usecase.ShortenedUrlUserEventsUseCases
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ShortenedUrlUserEventsConsumer(
    private val useCases: ShortenedUrlUserEventsUseCases
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(
        topics = ["shortened-url-events"],
    )
    fun consume(message: ConsumerRecord<String, ShortenedUrlUserEvents>) {
        val record = message.value()
        val instant = message.timestamp()
        when {
            record.userAccessedShortenedUrlEvent != null -> try {
                useCases.handleUserAccessedShortenedUrl(
                    event = record.userAccessedShortenedUrlEvent,
                )
            } catch (e: Throwable) {
                logger.error("Failed to process message: $e", e)
            }
        }
    }
}
