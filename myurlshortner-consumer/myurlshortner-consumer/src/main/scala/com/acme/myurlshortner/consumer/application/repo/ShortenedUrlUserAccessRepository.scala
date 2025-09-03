package com.acme.myurlshortner.consumer.application.repo
import zio.ZIO
import zio._
import com.acme.myurlshortner.consumer.domain.entity.userevent.ShortenedUrlUserAccess 


object ShortenedUrlUserAccessRepository {
  
  def saveShortenedUrlUserAccess(entity: ShortenedUrlUserAccess) = for {
    _ <- ZIO.logInfo(s"Saved: ${entity}")
  } yield (ZIO.unit)
}
