package com.acme.myurlshortner.consumer.domain.userevent.repo

import com.acme.myurlshortner.consumer.domain.userevent.entity.UserAccessedShortenedUrl

interface UserAccessedShortenedUrlRepo {
    suspend fun saveUserAccessedShortenedUrl(access: UserAccessedShortenedUrl)
    suspend fun countById(uid: String): Long
}
