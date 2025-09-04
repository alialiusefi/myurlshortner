package com.acme.myurlshortner.consumer.application

import com.acme.events.ShortenedUrlUserEvents
import com.acme.myurlshortner.consumer.application.config.KafkaConfig
import com.acme.myurlshortner.consumer.application.config.KafkaConfigLoader
import com.acme.myurlshortner.consumer.application.usecase.ShortenedUrlUserEventsUseCases
import io.apicurio.registry.serde.avro.AvroKafkaDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import zio.Scope
import zio.ZIO
import zio.ZIOAppArgs
import zio.ZIOAppDefault
import zio._
import zio.kafka.consumer.Consumer
import zio.kafka.consumer._
import zio.kafka.serde.Deserializer

object UserEventsConsumer extends ZIOAppDefault {

  def run: ZIO[ZIOAppArgs & Scope, Any, Unit] = for {
    config           <- KafkaConfigLoader.getKafkaConfigFromEnv()
    desConfigMap     <- KafkaConfigLoader.deserializerConfigMap()
    consumerSettings <- prepareConsumerSettings(config)
    keyDes           <- prepareKeyDeserializer(desConfigMap)
    valueDes         <- prepareValueDeserializer(desConfigMap)
    consumer         <- Consumer
                          .consumeWith(
                            settings = consumerSettings,
                            subscription = Subscription.topics(
                              config.shortenedUrlUserEventsTopic.topic
                            ),
                            keyDeserializer = keyDes,
                            valueDeserializer = valueDes,
                            commitRetryPolicy = Schedule.forever
                          ) { record =>
                            ShortenedUrlUserEventsUseCases
                              .handleUserAccessedShortenedUrl(record.value().userAccessedShortenedUrlEvent)
                              .fold(
                                fail => ZIO.logError(s"${fail}"),
                                success => ZIO.unit
                              )
                          }
                          .fork
    _                <- consumer.join
  } yield ()

  def prepareConsumerSettings(config: KafkaConfig): ZIO[Any, Nothing, ConsumerSettings] = ZIO.succeed(
    ConsumerSettings(
      bootstrapServers = config.bootstrapServers
    ).withGroupId(config.consumerGroup)
  )

  def prepareValueDeserializer(
    configMap: Map[String, String]
  ): ZIO[Any, Throwable, Deserializer[Any, ShortenedUrlUserEvents]] = for {
    deserializer <-
      Deserializer.fromKafkaDeserializer(AvroKafkaDeserializer[ShortenedUrlUserEvents](), configMap, false)
  } yield (deserializer)

  def prepareKeyDeserializer(configMap: Map[String, String]): ZIO[Any, Throwable, Deserializer[Any, String]] = for {
    deserializer <- Deserializer.fromKafkaDeserializer(StringDeserializer(), configMap, false)
  } yield (deserializer)
}
