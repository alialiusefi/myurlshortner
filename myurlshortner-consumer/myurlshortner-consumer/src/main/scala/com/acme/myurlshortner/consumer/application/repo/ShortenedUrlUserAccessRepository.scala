package com.acme.myurlshortner.consumer.application.repo

import com.acme.myurlshortner.consumer.domain.entity.userevent.ShortenedUrlUserAccess
import zio.ZIO
import zio._

class ShortenedUrlUserAccessRepository(
  //val transactor: DBTransactor
) {

  def saveShortenedUrlUserAccess(entity: ShortenedUrlUserAccess) = ???

}

object ShortenedUrlUserAccessRepository {
  def layer : ZLayer[Any, Throwable, ShortenedUrlUserAccessRepository] = ZLayer.succeed(ShortenedUrlUserAccessRepository())
}
