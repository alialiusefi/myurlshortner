package com.acme.myurlshortner.consumer.domain.userevent.repo

import com.acme.myurlshortner.consumer.domain.userevent.entity.UserAccessedShortenedUrl

interface UserAccessedShortenedUrlRepo {
    fun saveUserAccessedShortenedUrl(access: UserAccessedShortenedUrl)
}
