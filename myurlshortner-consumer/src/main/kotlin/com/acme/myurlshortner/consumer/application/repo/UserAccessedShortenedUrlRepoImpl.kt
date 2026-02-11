package com.acme.myurlshortner.consumer.application.repo

import com.acme.myurlshortner.consumer.application.repo.table.UserAccessedShortenedUrlTable
import com.acme.myurlshortner.consumer.domain.userevent.entity.UserAccessedShortenedUrl
import com.acme.myurlshortner.consumer.domain.userevent.repo.UserAccessedShortenedUrlRepo
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.v1.core.count
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class UserAccessedShortenedUrlRepoImpl(
) : UserAccessedShortenedUrlRepo {

    override suspend fun saveUserAccessedShortenedUrl(access: UserAccessedShortenedUrl) {
        newSuspendedTransaction(Dispatchers.IO) {
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

    @Transactional
    override suspend fun countById(uid: String): Long = newSuspendedTransaction(Dispatchers.IO) {
        UserAccessedShortenedUrlTable.select(UserAccessedShortenedUrlTable.uniqueIdentifier.count()).where {
            UserAccessedShortenedUrlTable.uniqueIdentifier eq uid
        }.count()
    }
}
