package com.acme.myurlshortner.consumer.application.repo

import com.acme.myurlshortner.consumer.domain.entity.userevent.ShortenedUrlUserAccess
import zio.ZIO
import zio._

object ShortenedUrlUserAccessRepository {

  def saveShortenedUrlUserAccess(entity: ShortenedUrlUserAccess): ZIO[Any, Nothing, UIO[Unit]] = for {
    _ <- ZIO.logInfo(s"Saved: ${entity}")
  } yield (ZIO.unit)
}
