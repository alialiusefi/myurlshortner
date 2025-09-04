package com.acme.myurlshortner.consumer.application.config

import zio.ConfigProvider
import zio._
import zio.Exit.Success
import zio.Exit.Failure
import io.apicurio.registry.serde.SerdeConfig
import io.apicurio.registry.serde.avro.AvroKafkaSerdeConfig
import io.apicurio.registry.serde.avro.ReflectAvroDatumProvider

case class KafkaConfig(
  val bootstrapServers: List[String],
  val consumerGroup: String,
  val shortenedUrlUserEventsTopic: KafkaTopicConfig
)

case class KafkaTopicConfig(
  val topic: String
)

object KafkaConfigLoader {
  def getKafkaConfigFromEnv(): ZIO[Any, SecurityException, KafkaConfig] = for {
    urls                        <- System.env("BOOTSTRAP_URLS").map(op => op.get.split(",").toList)
    consumerGroup               <- System.env("CONSUMER_GROUP")
    shortenedUrlUserEventsTopic <- System.env("SHORTENED_URL_USER_EVENTS_TOPIC")
  } yield (KafkaConfig(urls, consumerGroup.get, KafkaTopicConfig(shortenedUrlUserEventsTopic.get)))

  // https://www.apicur.io/registry/docs/apicurio-registry/3.0.x/getting-started/assembly-configuring-kafka-client-serdes.html
  def deserializerConfigMap() = for {
    registryUrl <- System.env("REGISTRY_URL")
    map         <- ZIO.succeed(
                     Map(
                       SerdeConfig.REGISTRY_URL                      -> registryUrl.get,
                       SerdeConfig.EXPLICIT_ARTIFACT_GROUP_ID        -> "com.acme.events",
                       SerdeConfig.EXPLICIT_ARTIFACT_ID              -> "ShortenedUrlUserEvents",
                       SerdeConfig.EXPLICIT_ARTIFACT_VERSION         -> "1",
                       AvroKafkaSerdeConfig.USE_SPECIFIC_AVRO_READER -> "true",
                       AvroKafkaSerdeConfig.AVRO_DATUM_PROVIDER      -> "io.apicurio.registry.serde.avro.ReflectAvroDatumProvider"
                     )
                   )
  } yield (map)
}
