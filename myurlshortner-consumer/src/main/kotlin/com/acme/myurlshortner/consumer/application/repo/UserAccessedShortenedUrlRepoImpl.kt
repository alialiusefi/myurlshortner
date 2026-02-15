package com.acme.myurlshortner.consumer.application.repo

import com.acme.myurlshortner.consumer.application.repo.table.UserAccessedShortenedUrlTable
import com.acme.myurlshortner.consumer.domain.userevent.entity.UserAccessedShortenedUrl
import com.acme.myurlshortner.consumer.domain.userevent.repo.UserAccessedShortenedUrlRepo
import org.jetbrains.exposed.v1.core.count
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.springframework.stereotype.Repository

@Repository
class UserAccessedShortenedUrlRepoImpl(
) : UserAccessedShortenedUrlRepo {

    override suspend fun saveUserAccessedShortenedUrl(access: UserAccessedShortenedUrl) {
        suspendTransaction {
            UserAccessedShortenedUrlTable.insert {
                it[uniqueIdentifier] = access.uniqueIdentifier
                it[browser] = access.browser.toString()
                it[operatingSystem] = access.operatingSystem.toString()
                it[device] = access.device.toString()
                it[shortenedUrl] = access.shortenedUrl.toString()
                it[originalUrl] = access.originalUrl.toString()
                it[accessedAt] = access.accessedAt
            }
        }
    }


    override suspend fun countById(uid: String): Long = suspendTransaction {
        UserAccessedShortenedUrlTable.select(UserAccessedShortenedUrlTable.uniqueIdentifier.count()).where {
            UserAccessedShortenedUrlTable.uniqueIdentifier eq uid
        }.count()
    }
}
