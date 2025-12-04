package com.acme.myurlshortner.consumer.domain.userevent.service

import com.acme.myurlshortner.consumer.domain.userevent.command.UserAccessedShortenedUrlCommand

interface UserAccessedShortenedUrlEventService {
    /**
     * Business logic of post accessing a shortened url.
     * User Agent parsing is following the specs below:
     * 1. https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/User-Agent
     * 2. https://developers.google.com/search/docs/crawling-indexing/google-common-crawlers
     */
    fun handleShortenedUrlUserAccessed(
        command: UserAccessedShortenedUrlCommand,
    )
}
