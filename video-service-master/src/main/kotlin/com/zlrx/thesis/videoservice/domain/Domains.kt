package com.zlrx.thesis.videoservice.domain

import com.chrylis.codec.base58.Base58UUID
import com.fasterxml.jackson.annotation.JsonView
import com.zlrx.thesis.videoservice.controller.view.NoneView
import com.zlrx.thesis.videoservice.controller.view.StudentView
import com.zlrx.thesis.videoservice.controller.view.TeacherView
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.util.UUID

fun newId(): String = Base58UUID().encode(UUID.randomUUID())

abstract class TenantAwareEntity {
    @Indexed
    lateinit var tenantId: String
}

@Document(collection = "video_metadata")
data class VideoMetadata(

    @Id
    @get:JsonView(StudentView::class)
    val id: String,

    @get:JsonView(StudentView::class)
    val title: String,

    @Indexed
    @get:JsonView(StudentView::class)
    val fileName: String,

    @get:JsonView(NoneView::class)
    val originalFileName: String,

    @get:JsonView(TeacherView::class)
    val public: Boolean,

    @get:JsonView(TeacherView::class)
    val archived: Boolean = false,

    @get:JsonView(StudentView::class)
    val thumbnail: String? = null,

    @Version
    @get:JsonView(NoneView::class)
    var version: Long = 0,

    @CreatedDate
    @get:JsonView(NoneView::class)
    var createdAt: Instant? = null,

    @LastModifiedDate
    @get:JsonView(NoneView::class)
    var modifiedAt: Instant? = null,

    @CreatedBy
    @Indexed(sparse = true)
    @get:JsonView(TeacherView::class)
    var createdBy: String? = null,

    @LastModifiedBy
    @get:JsonView(NoneView::class)
    var modifiedBy: String? = null

) : TenantAwareEntity()
