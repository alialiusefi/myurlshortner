package com.acme.myurlshortner.consumer

import zio._
import zio.ZIOApp
import zio.{Scope, ZIO, ZIOAppArgs}
import zio.ZLayer
import zio.Executor
import zio.Runtime
import zio.ZIOAppDefault
import zio.kafka.consumer._
import zio.kafka.consumer.Consumer
import com.acme.myurlshortner.consumer.config.KafkaConfigLoader

object UserEventsConsumer extends ZIOAppDefault {

  def run: ZIO[ZIOAppArgs & Scope, Any, Unit] = for {
    config           <- KafkaConfigLoader.getFromEnv()
    consumerSettings <- ZIO.succeed(
                          ConsumerSettings(
                            bootstrapServers = config.bootstrapServers
                          ).withGroupId(config.consumerGroup)
                        )
    consumer <- Consumer.consumeWith(
        settings = consumerSettings,
        subscription = Subscription.topics(
          config.topicConfigs.head.topic
        ),
        keyDeserializer = null,
        valueDeserializer = null
      ) { record => 
        Console.printLine(s"Consumed ${record.key()}, ${record.value()}").orDie
      }.fork
     _ <- consumer.join
  } yield ()
}
