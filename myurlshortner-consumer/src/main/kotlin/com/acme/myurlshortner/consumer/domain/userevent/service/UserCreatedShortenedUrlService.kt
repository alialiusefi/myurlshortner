package com.acme.myurlshortner.consumer.domain.userevent.service

import com.acme.myurlshortner.consumer.domain.userevent.command.UserCreatedShortenedUrlCommand

interface UserCreatedShortenedUrlService {

    /**
     * Business logic of post creating a shortened url.
     */
    fun handleShortenedUrlCreated(command: UserCreatedShortenedUrlCommand)
}
