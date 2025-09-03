package com.acme.myurlshortner.consumer.domain.service

import com.acme.myurlshortner.consumer.domain.command.SaveShortenedUrlUserAccessCommand
import zio.ZIO

trait ShortenedUrlUserAccessService {
    def saveUserAccess(command: SaveShortenedUrlUserAccessCommand): ZIO[Any, Nothing, Unit]
}
