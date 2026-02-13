package com.acme.myurlshortner.consumer.application.kafka.consumer

import com.acme.events.ShortenedUrlUserEvents
import com.acme.myurlshortner.consumer.application.kafka.retry.ShortenedUrlUserEventsRetry
import com.acme.myurlshortner.consumer.application.usecase.ShortenedUrlUserEventsUseCases
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import org.springframework.stereotype.Component
import reactor.kafka.receiver.ReceiverRecord
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

@Component
@ConditionalOnProperty(name = ["app.kafka.consumer.enabled"], havingValue = "true")
class ShortenedUrlUserEventsReactiveConsumer(
    private val template: ReactiveKafkaConsumerTemplate<String?, ShortenedUrlUserEvents>,
    private val useCases: ShortenedUrlUserEventsUseCases,
    private val retry: ShortenedUrlUserEventsRetry
) {
    private var jobReference: Job? = null
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Value($$"${app.podname}")
    lateinit var podName: String

    private val consumerFlow = template.receive().asFlow().flowOn(Dispatchers.Default)

    @OptIn(DelicateCoroutinesApi::class)
    @PostConstruct
    fun subscribeConsumer() {
        logger.info("Setting up consumer.")
        jobReference = GlobalScope.launch(Dispatchers.Default) {
            consumerFlow.onEach { record ->
                doConsume(record)
            }.onCompletion {
                logger.info("Consumption from kafka stopped.")
            }.catch { ex ->
                // todo handle deserialization error case
                logger.error("Unhandled exception! Will attempt to restart. cause: ${ex.cause}", ex)
                subscribeConsumer()
            }.collect()
        }
        logger.info("Consumer set.")
    }

    private suspend fun doConsume(message: ReceiverRecord<String?, ShortenedUrlUserEvents>) {
        logger.debug("Message: {}", message)
        logger.debug("Running on: {}", Thread.currentThread())
        logger.debug("currentCoroutineContext(): {}", currentCoroutineContext())
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

            record.userCreatedShortenedUrlEvent != null && record.userCreatedShortenedUrlEvent.userId != null -> try {
                useCases.handleUserCreatedShortenedUrl(record.userCreatedShortenedUrlEvent)
            } catch (e: Throwable) {
                logger.error("Failed to process message: $e", e)
                retry.handleFailedKafkaUserCreatedShortenedUrlEvent(record.userCreatedShortenedUrlEvent)
            }
        }
        message.receiverOffset().commit()
    }

    @PreDestroy
    fun cleanup() {
        logger.info("Cleaning up consumer.")
        jobReference?.cancel("App Termination.", null)
    }
}