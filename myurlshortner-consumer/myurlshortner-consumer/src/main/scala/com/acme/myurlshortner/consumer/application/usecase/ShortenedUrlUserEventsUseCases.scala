package com.acme.myurlshortner.consumer.application.usecase

import com.acme.events.UserAccessedShortenedUrl
import zio._
import java.net.URI
import zio.Exit.Success
import zio.Exit.Failure
import com.acme.myurlshortner.consumer.application.service.ShortenedUrlUserAccessServiceImpl
import com.acme.myurlshortner.consumer.domain.command.SaveShortenedUrlUserAccessCommand

object ShortenedUrlUserEventsUseCases {
  
  def handleUserAccessedShortenedUrl(event: UserAccessedShortenedUrl): ZIO[Any, Throwable, Unit] = for {
    _ <- ZIO.logInfo(s"${event}")
    originalUrl <- ZIO.attempt(URI.create(event.original_url.toString()))
    shortenedUrl <- ZIO.attempt(URI.create(event.shortened_url.toString()))
    userAgent <- ZIO.succeed(event.user_agent.toString())
    _ <- ShortenedUrlUserAccessServiceImpl.saveUserAccess(
      SaveShortenedUrlUserAccessCommand(userAgent, originalUrl, shortenedUrl)
    )
  } yield (ZIO.unit)
}
