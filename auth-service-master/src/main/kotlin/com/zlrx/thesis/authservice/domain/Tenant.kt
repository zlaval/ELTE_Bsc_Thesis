package com.zlrx.thesis.authservice.domain

import com.zlrx.thesis.authservice.config.TENANT_COLLECTION
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = TENANT_COLLECTION)
data class Tenant(

    @Id
    val id: String,

    @Indexed(unique = true)
    val name: String,

    val displayName: String,

    @Version
    var version: Long = 0,

    @CreatedDate
    var createdAt: Instant? = null,

    @CreatedBy
    var createdBy: String? = null,

    @LastModifiedDate
    var modifiedAt: Instant? = null,

    @LastModifiedBy
    var modifiedBy: String? = null

)
