package com.zlrx.thesis.gradeservice.domain

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

abstract class TenantAwareEntity {
    @Indexed
    lateinit var tenantId: String
}

@Document(collection = "grade")
data class Grade(
    @Id
    val id: String,

    @Indexed
    val subjectId: String,

    val userId: String,

    val mark: Int,

    val points: Int,

    val credit: Int,

    @Version
    var version: Long = 0,

    @CreatedDate
    var createdAt: Instant? = null,

    @LastModifiedDate
    var modifiedAt: Instant? = null

) : TenantAwareEntity() {
    companion object
}

@Document(collection = "assessment")
data class Assessment(
    @Id
    val id: String,

    @Indexed
    val subjectId: String,

    val lessonId: String,

    @Indexed
    val studentId: String,

    val points: Int,

    val maxPoints: Int,

    @Indexed(unique = true)
    val solutionId: String,

    @Version
    var version: Long = 0,

    @CreatedDate
    var createdAt: Instant? = null,

    @LastModifiedDate
    var modifiedAt: Instant? = null,
) : TenantAwareEntity() {
    companion object
}
