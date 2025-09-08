package com.acme.myurlshortner.consumer.application.repo

import com.acme.myurlshortner.consumer.application.repo.table.UserAccessedShortenedUrlTable
import com.acme.myurlshortner.consumer.domain.userevent.entity.UserAccessedShortenedUrl
import com.acme.myurlshortner.consumer.domain.userevent.repo.UserAccessedShortenedUrlRepo
import org.jetbrains.exposed.v1.jdbc.insert
import org.springframework.stereotype.Repository

@Repository
class UserAccessedShortenedUrlRepoImpl(
) : UserAccessedShortenedUrlRepo {
    override fun saveUserAccessedShortenedUrl(access: UserAccessedShortenedUrl) {
        UserAccessedShortenedUrlTable.insert {
            it[browser] = access.browser.toString()
            it[operatingSystem] = access.operatingSystem.toString()
            it[device] = access.device.toString()
            it[shortenedUrl] = access.shortenedUrl.toString()
            it[originalUrl] = access.originalUrl.toString()
            it[accessedAt] = access.accessedAt
        }
    }
}