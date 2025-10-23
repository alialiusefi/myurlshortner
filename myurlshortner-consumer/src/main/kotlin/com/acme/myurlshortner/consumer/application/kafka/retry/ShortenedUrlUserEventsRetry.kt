package com.acme.myurlshortner.consumer.application.kafka.retry

import com.acme.myurlshortner.consumer.application.repo.KafkaRetryQueueRepository
import com.acme.myurlshortner.consumer.application.usecase.ShortenedUrlUserEventsUseCases
import org.apache.avro.io.DatumWriter
import org.apache.avro.io.DecoderFactory
import org.apache.avro.io.EncoderFactory
import org.apache.avro.specific.SpecificDatumReader
import org.apache.avro.specific.SpecificDatumWriter
import org.jboss.logging.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.io.ByteArrayOutputStream
import java.time.OffsetDateTime
import com.acme.events.UserAccessedShortenedUrl as AvroUserAccessedShortenedUrl

@Component
class ShortenedUrlUserEventsRetry(
    private val repository: KafkaRetryQueueRepository,
    private val useCases: ShortenedUrlUserEventsUseCases
) {
    private val logger = Logger.getLogger(ShortenedUrlUserEventsRetry::class.java)

    @Value($$"${app.kafka.enabled}")
    lateinit var topic: String

    fun handleFailedKafkaUserAccessedShortenedUrlEvent(event: AvroUserAccessedShortenedUrl) {
        val writer: DatumWriter<AvroUserAccessedShortenedUrl> =
            SpecificDatumWriter(AvroUserAccessedShortenedUrl.getClassSchema())
        val byteOutputStream = ByteArrayOutputStream()
        val encoder = EncoderFactory.get().jsonEncoder(AvroUserAccessedShortenedUrl.getClassSchema(), byteOutputStream)
        byteOutputStream.use {
            writer.write(event, encoder)
        }
        encoder.flush()
        val json = byteOutputStream.toString()
        repository.insertFailedEvent(
            event = json,
            eventDateTime = OffsetDateTime.parse(event.accessedAt),
            key = event.uniqueIdentifier,
            type = KafkaEventType.USER_ACCESSED_SHORTENED_URL,
            version = 6,
            topic = topic
        )
    }

    @Scheduled(fixedRate = 5000)
    @Transactional
    fun retryFailedKafkaEvents() {
        val failedEvent = repository.fetchLatestFailedEvent(topic)
        if (failedEvent != null) {
            try {
                logger.info("Retrying failed event id=${failedEvent.id} type=${failedEvent.eventType} with retryCount=${failedEvent.retryCount}")
                when (failedEvent.eventType) {
                    KafkaEventType.USER_ACCESSED_SHORTENED_URL -> {
                        val decoder = DecoderFactory.get().jsonDecoder(
                            AvroUserAccessedShortenedUrl.getClassSchema(),
                            failedEvent.event
                        )
                        val reader: SpecificDatumReader<AvroUserAccessedShortenedUrl> =
                            SpecificDatumReader(AvroUserAccessedShortenedUrl.getClassSchema())
                        val record = reader.read(null, decoder)
                        useCases.handleUserAccessedShortenedUrl(record)
                    }
                }
            } catch (e: Throwable) {
                logger.error("Retry failed for event=${failedEvent.id}", e)
                repository.updatedRetryCount(failedEvent.id, failedEvent.retryCount + 1)
                return
            }
            repository.deleteFailedEvent(failedEvent.id)
        }
    }
}
