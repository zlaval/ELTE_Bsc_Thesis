package com.zlrx.thesis.authservice.domain

import com.zlrx.thesis.authservice.config.USER_COLLECTION
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

enum class Role {
    ROLE_ADMIN, ROLE_TEACHER, ROLE_STUDENT, ROLE_SYSTEM;

    companion object {
        fun toRole(value: Any?) = when (value) {
            "ROLE_ADMIN" -> ROLE_ADMIN
            "ROLE_TEACHER" -> ROLE_TEACHER
            "ROLE_STUDENT" -> ROLE_STUDENT
            else -> ROLE_SYSTEM
        }
    }
}

abstract class TenantAwareEntity {
    @Indexed
    lateinit var tenantId: String
}

@Document(collection = USER_COLLECTION)
@CompoundIndex(name = "email_tenant_index", def = "{'email': 1, 'tenantId': 1}", unique = true)
data class User(
    @Id
    val id: String,
    val email: String,
    val name: String,
    val password: String,

    val role: Role = Role.ROLE_STUDENT,

    val profilePicture: String?,

    @Version
    var version: Long = 0,

    @CreatedDate
    var createdAt: Instant? = null,

    @CreatedBy
    var createdBy: String? = null,

    @LastModifiedDate
    var modifiedDate: Instant? = null,

    @LastModifiedBy
    var modifiedBy: String? = null,
) : TenantAwareEntity()
