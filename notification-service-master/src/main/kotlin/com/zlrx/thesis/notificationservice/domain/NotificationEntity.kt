package com.zlrx.thesis.notificationservice.domain

import com.chrylis.codec.base58.Base58UUID
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.util.UUID

fun newId() = Base58UUID().encode(UUID.randomUUID())

enum class Status {
    NEW, READ
}

@Document(collection = "notification")
data class NotificationEntity(
    val id: String,
    val userId: String,
    val title: String,
    val text: String,
    val status: Status,

    @Version
    var version: Long = 0,

    @CreatedDate
    var createdAt: Instant? = null

) {
    companion object
}
