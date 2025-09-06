package com.acme.myurlshortner.consumer.application.usecase

import com.acme.events.UserAccessedShortenedUrl
import com.acme.myurlshortner.consumer.domain.command.SaveShortenedUrlUserAccessCommand
import com.acme.myurlshortner.consumer.domain.service.ShortenedUrlUserEventsService
import zio._

import java.net.URI

class ShortenedUrlUserEventsUseCases(
  val service: ShortenedUrlUserEventsService
) {

  def handleUserAccessedShortenedUrl(event: UserAccessedShortenedUrl): ZIO[Any, Throwable, Unit] = for {
    originalUrl  <- ZIO.attempt(URI.create(event.original_url.toString()))
    shortenedUrl <- ZIO.attempt(URI.create(event.shortened_url.toString()))
    userAgent    <- ZIO.succeed(event.user_agent.toString())
    _            <- service.saveUserAccess(
                      SaveShortenedUrlUserAccessCommand(userAgent, originalUrl, shortenedUrl)
                    )
  } yield (ZIO.unit)
}

object ShortenedUrlUserEventsUseCases {
  val layer: ZLayer[ShortenedUrlUserEventsService, Nothing, ShortenedUrlUserEventsUseCases] = ZLayer.fromZIO(
    for {
      service <- ZIO.service[ShortenedUrlUserEventsService]
    } yield (ShortenedUrlUserEventsUseCases(service))
  )
}
