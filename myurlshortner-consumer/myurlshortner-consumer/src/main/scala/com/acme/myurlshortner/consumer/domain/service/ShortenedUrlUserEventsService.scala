package com.acme.myurlshortner.consumer.domain.service

import com.acme.myurlshortner.consumer.domain.command.SaveShortenedUrlUserAccessCommand
import com.acme.myurlshortner.consumer.domain.entity.userevent.ShortenedUrlUserAccess
import zio.ZIO

trait ShortenedUrlUserEventsService {
  def saveUserAccess(command: SaveShortenedUrlUserAccessCommand): ZIO[Any, Nothing, ShortenedUrlUserAccess]
}
