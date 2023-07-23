package com.zlrx.thesis.enrollmentservice.domain

import com.chrylis.codec.base58.Base58UUID
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.util.UUID

fun newId() = Base58UUID().encode(UUID.randomUUID())

enum class EnrollmentStatus {
    IN_PROGRESS, ENROLLED, DROPPED, NO_SEATS, ERROR, FINISHED, CLOSED
}

@Document(collection = "enrollment")
data class Enrollment(
    @Id
    val id: String,

    @Indexed
    val subjectId: String,

    @Indexed
    val studentId: String,

    val status: EnrollmentStatus,

    val tenantId: String,

    @Version
    var version: Long = 0,

    @CreatedDate
    var createdAt: Instant? = null,

    @LastModifiedDate
    var modifiedAt: Instant? = null,

    )
