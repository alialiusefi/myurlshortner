package com.acme.myurlshortner.consumer.application.repo.table

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestampWithTimeZone
import org.jetbrains.exposed.v1.json.json

object NotificationTable : Table("notifications") {
    val id = long("id").autoIncrement()
    val uniqueIdentifier = varchar("unique_identifier", 10)
    val type = varchar("type", 64)
    val params = json("params", { a -> a }, { a -> a })
    val userId = long("user_id")
    val createdAt = timestampWithTimeZone("created_at")
    val readAt = timestampWithTimeZone("read_at").nullable()
}
