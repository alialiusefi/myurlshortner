package com.acme.myurlshortner.consumer.config

import zio.ConfigProvider
import zio._
import zio.Exit.Success
import zio.Exit.Failure

case class KafkaConfig(
  val bootstrapServers: List[String],
  val registryUrl: String,
  val consumerGroup: String,
  val artifactGroupId: String,
  val shortenedUrlUserEventsTopic: KafkaTopicConfig,
)

case class KafkaTopicConfig(
  val topic: String
)

object KafkaConfigLoader {
  def getFromEnv(): ZIO[Any, SecurityException, KafkaConfig] = for {
    urls            <- System.env("BOOTSTRAP_URLS").map(op => op.get.split(",").toList)
    registryUrl     <- System.env("REGISTRY_URL")
    consumerGroup   <- System.env("CONSUMER_GROUP")
    artifactGroupId <- System.env("ARTIFACT_GROUP_ID")
    shortenedUrlUserEventsTopic <- System.env("SHORTENED_URL_USER_EVENTS_TOPIC")
  } yield (KafkaConfig(urls, registryUrl.get, artifactGroupId.get, consumerGroup.get, KafkaTopicConfig(shortenedUrlUserEventsTopic.get)))
}
