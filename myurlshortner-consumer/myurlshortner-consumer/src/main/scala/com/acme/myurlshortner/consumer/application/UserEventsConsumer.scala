package com.acme.myurlshortner.consumer.application

import zio._
import zio.ZIOApp
import zio.{Scope, ZIO, ZIOAppArgs}
import zio.ZLayer
import zio.Executor
import zio.Runtime
import zio.ZIOAppDefault
import zio.kafka.consumer._
import zio.kafka.consumer.Consumer
import com.acme.myurlshortner.consumer.application.config.KafkaConfigLoader
import zio.kafka.serde.Deserializer
import io.apicurio.registry.serde.avro.AvroKafkaDeserializer
import scala.caps.consume
import com.acme.myurlshortner.consumer.application.config.KafkaConfig
import com.acme.events.ShortenedUrlUserEvents
import org.apache.kafka.common.serialization.StringDeserializer
import com.acme.myurlshortner.consumer.application.usecase.ShortenedUrlUserEventsUseCases


object UserEventsConsumer extends ZIOAppDefault {

  def run: ZIO[ZIOAppArgs & Scope, Any, Unit] = for {
    config           <- KafkaConfigLoader.getKafkaConfigFromEnv()
    consumerSettings <- prepareConsumerSettings(config)
    keyDes <- prepareKeyDeserializer()
    valueDes <- prepareValueDeserializer()
    consumer <- Consumer.consumeWith(
        settings = consumerSettings,
        subscription = Subscription.topics(
          config.shortenedUrlUserEventsTopic.topic
        ),
        keyDeserializer = keyDes,
        valueDeserializer = valueDes,
        commitRetryPolicy = Schedule.forever,
      ) { record => ShortenedUrlUserEventsUseCases.handleUserAccessedShortenedUrl(record.value().userAccessedShortenedUrlEvent)
      .fold(
        fail => ZIO.logError(s"${fail}"),
        success => ZIO.unit
      )
      }.fork
     _ <- consumer.join
  } yield ()

  def prepareConsumerSettings(config: KafkaConfig) = ZIO.succeed(
                          ConsumerSettings(
                            bootstrapServers = config.bootstrapServers
                          ).withGroupId(config.consumerGroup)
                        )

  def prepareValueDeserializer() = for {
    map <- KafkaConfigLoader.deserializerConfigMap()
    deserializer <- Deserializer.fromKafkaDeserializer(AvroKafkaDeserializer[ShortenedUrlUserEvents](), map, false)
  } yield (deserializer)

  def prepareKeyDeserializer() = for {
    map <- KafkaConfigLoader.deserializerConfigMap()
    deserializer <- Deserializer.fromKafkaDeserializer(StringDeserializer(), map, false)
  } yield (deserializer)
}
