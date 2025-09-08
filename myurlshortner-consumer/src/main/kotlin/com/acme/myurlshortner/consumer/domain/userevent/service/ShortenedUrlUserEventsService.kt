package com.acme.myurlshortner.consumer.domain.userevent.service

import com.acme.myurlshortner.consumer.domain.userevent.command.UserAccessedShortenedUrlCommand

interface ShortenedUrlUserEventsService {
    fun handleShortenedUrlUserAccessed(
        command: UserAccessedShortenedUrlCommand,
    )
}
