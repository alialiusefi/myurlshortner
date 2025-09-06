package com.acme.myurlshortner.consumer.application.service

import com.acme.myurlshortner.consumer.application.repo.ShortenedUrlUserAccessRepository
import com.acme.myurlshortner.consumer.domain.command.SaveShortenedUrlUserAccessCommand
import com.acme.myurlshortner.consumer.domain.entity.useragent._
import com.acme.myurlshortner.consumer.domain.entity.userevent.ShortenedUrlUserAccess
import com.acme.myurlshortner.consumer.domain.service.ShortenedUrlUserEventsService
import zio.ZIO
import zio.ZLayer

class ShortenedUrlUserEventsServiceImpl(
  val shortenedUrlUserAccessRepository: ShortenedUrlUserAccessRepository
) extends ShortenedUrlUserEventsService {

  override def saveUserAccess(command: SaveShortenedUrlUserAccessCommand): ZIO[Any, Throwable, Unit] =
    for {
      browser    <- ZIO.succeed(
                      command.userAgent match
                        case a if a.contains(MozillaFirefox.userAgentValue) => MozillaFirefox
                        case b if b.contains(Chrome.userAgentValue)         => Chrome
                        case c if c.contains(Safari.userAgentValue)         => Safari
                        case _                                              => OtherBrowser
                    )
      device     <- ZIO.succeed(
                      command.userAgent match
                        case a if a.contains(Mac.userAgentValue)     => Mac
                        case b if b.contains(iPad.userAgentValue)    => iPad
                        case c if c.contains(iPhone.userAgentValue)  => iPhone
                        case d if d.contains(Android.userAgentValue) => Android
                        case _                                       => PC
                    )
      os         <- ZIO.succeed(
                      command.userAgent match
                        case a if a.contains(Windows.userAgentValue)   => Windows
                        case b if b.contains(Macintosh.userAgentValue) => Macintosh
                        case c if c.contains(Linux.userAgentValue)     => Linux
                        case d                                         => OtherOS
                    )
      userAccess <- ZIO.succeed(
                      ShortenedUrlUserAccess(
                        browser = browser,
                        device = device,
                        os = os,
                        shortenedUrl = command.shortenedUrl,
                        originalUrl = command.originalUrl
                      )
                    )
                    _ <- ZIO.log("here")
      _          <- shortenedUrlUserAccessRepository.saveShortenedUrlUserAccess(
                      userAccess
                    )
    } yield ()
}

object ShortenedUrlUserEventsService {
  val layer: ZLayer[ShortenedUrlUserAccessRepository, Throwable, ShortenedUrlUserEventsService] = ZLayer.fromZIO(
    for {
      repo <- ZIO.service[ShortenedUrlUserAccessRepository]
    } yield (ShortenedUrlUserEventsServiceImpl(repo))
  )
}
