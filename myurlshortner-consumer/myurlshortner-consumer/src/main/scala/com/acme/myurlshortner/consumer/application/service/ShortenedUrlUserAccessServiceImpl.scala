package com.acme.myurlshortner.consumer.application.service

import com.acme.myurlshortner.consumer.domain.service.ShortenedUrlUserAccessService
import com.acme.myurlshortner.consumer.domain.command.SaveShortenedUrlUserAccessCommand
import zio.ZIO
import com.acme.myurlshortner.consumer.application.repo.ShortenedUrlUserAccessRepository
import com.acme.myurlshortner.consumer.domain.entity.useragent._
import com.acme.myurlshortner.consumer.domain.entity.userevent.ShortenedUrlUserAccess

object ShortenedUrlUserAccessServiceImpl extends ShortenedUrlUserAccessService {

  override def saveUserAccess(command: SaveShortenedUrlUserAccessCommand) = for {
    browser <- ZIO.succeed(
                 command.userAgent match
                   case a if a.contains(MozillaFirefox.userAgentValue) => MozillaFirefox
                   case b if b.contains(Chrome.userAgentValue)         => Chrome
                   case c if c.contains(Safari.userAgentValue)         => Safari
                   case _                                              => OtherBrowser
               )
    device  <- ZIO.succeed(
                 command.userAgent match
                   case a if a.contains(Mac.userAgentValue)     => Mac
                   case b if b.contains(iPad.userAgentValue)    => iPad
                   case c if c.contains(iPhone.userAgentValue)  => iPhone
                   case d if d.contains(Android.userAgentValue) => Android
                   case _                                       => PC
               )
    os      <- ZIO.succeed(
                 command.userAgent match
                   case a if a.contains(Windows.value)   => Windows
                   case b if b.contains(Macintosh.value) => Macintosh
                   case c if c.contains(Linux.value)     => Linux
                   case d                                => OtherOS
               )
    _       <- ShortenedUrlUserAccessRepository.saveShortenedUrlUserAccess(
                 ShortenedUrlUserAccess(
                   browser = browser,
                   device = device,
                   os = os,
                   shortenedUrl = command.shortenedUrl,
                   originalUrl = command.originalUrl
                 )
               )
  } yield (ZIO.unit)
}
