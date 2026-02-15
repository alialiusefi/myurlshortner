package com.acme.myurlshortner.consumer.application.kafka.config

import com.acme.events.ShortenedUrlUserEvents
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import org.springframework.stereotype.Component
import reactor.kafka.receiver.ReceiverOptions

@Component
class KafkaConfig(
    val custom: CustomKafkaProperties
) {
    @Component
    data class CustomKafkaProperties(
        @Value($$"${app.kafka.bootstrap-servers}")
        val servers: String,
        @Value($$"${app.kafka.consumer.group-id}")
        val groupId: String,
        @Value($$"${app.kafka.consumer.client-id}")
        val clientId: String,
        @Value($$"${app.kafka.consumer.topic-name}")
        val topicName: String,
        @Value($$"${app.kafka.consumer.user-events.apicurio.registry.url}")
        val registryUrl: String,
    )

    @Bean
    fun reactiveKafkaConsumerTemplate(): ReactiveKafkaConsumerTemplate<String?, ShortenedUrlUserEvents> {
        val options = ReceiverOptions.create<String?, ShortenedUrlUserEvents>(buildMap {
            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, custom.servers)
            put(ConsumerConfig.GROUP_ID_CONFIG, custom.groupId)
            put(ConsumerConfig.CLIENT_ID_CONFIG, custom.clientId)
            put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer"
            )
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.apicurio.registry.serde.avro.AvroKafkaDeserializer")
            put("apicurio.registry.url", custom.registryUrl)
            put("apicurio.registry.avro-datum-provider", "io.apicurio.registry.serde.avro.ReflectAvroDatumProvider")
            put("apicurio.registry.use-specific-avro-reader", true)
        }).subscription(listOf(custom.topicName))
        return ReactiveKafkaConsumerTemplate(options)
    }
}