package com.zlrx.thesis.subjectservice.domain

import com.chrylis.codec.base58.Base58UUID
import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonManagedReference
import com.fasterxml.jackson.annotation.JsonView
import com.zlrx.thesis.subjectservice.config.authInfo
import com.zlrx.thesis.subjectservice.config.orBlow
import com.zlrx.thesis.subjectservice.controller.view.StudentView
import com.zlrx.thesis.subjectservice.controller.view.TeacherView
import com.zlrx.thesis.subjectservice.controller.view.TransientView
import jakarta.persistence.Basic
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.ManyToOne
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.NamedAttributeNode
import jakarta.persistence.NamedEntityGraph
import jakarta.persistence.NamedEntityGraphs
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import jakarta.persistence.Transient
import jakarta.persistence.Version
import org.hibernate.annotations.Filter
import org.hibernate.annotations.FilterDef
import org.hibernate.annotations.ParamDef
import org.hibernate.annotations.TenantId
import org.hibernate.annotations.Where
import java.time.Instant
import java.util.UUID

const val SYSTEM = "system"
fun newId() = Base58UUID().encode(UUID.randomUUID())

@FilterDef(
    name = "creatorFilter",
    defaultCondition = "created_by = :userId",
    parameters = [
        ParamDef(name = "userId", type = String::class)
    ]
)
@MappedSuperclass
abstract class TenantAwareBaseEntity {

    @jakarta.persistence.Id
    @JsonView(StudentView::class)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    var id: String = newId()

    @Version
    @JsonView(TransientView::class)
    var version: Long? = null

    @JsonView(TransientView::class)
    var createdAt: Instant? = null

    @JsonView(TransientView::class)
    var modifiedAt: Instant? = null

    @JsonView(TransientView::class)
    var createdBy: String? = null

    @JsonView(TransientView::class)
    var modifiedBy: String? = null

    @TenantId
    @Column(name = "tenantId", nullable = false, updatable = false)
    @JsonView(TransientView::class)
    var tenantId: String? = null

    @PrePersist
    fun prePersist() {
        createdAt = Instant.now()
        modifiedAt = createdAt
        createdBy = authInfo()?.id ?: SYSTEM
        modifiedBy = createdBy
        tenantId = authInfo().orBlow().tenantId
    }

    @PreUpdate
    fun preUpdate() {
        modifiedAt = Instant.now()
        modifiedBy = authInfo()?.id ?: SYSTEM
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TenantAwareBaseEntity

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

@Filter(name = "creatorFilter")
@Entity
@Table(name = "subject", indexes = [Index(columnList = "tenantId")])
@NamedEntityGraphs(
    value = [
        NamedEntityGraph(
            name = "Subject.lessons",
            attributeNodes = [
                NamedAttributeNode("lessons")
            ],
        ),
        NamedEntityGraph(name = "Subject.noJoin")
    ]
)
class Subject : TenantAwareBaseEntity() {

    @Basic(optional = false)
    @JsonView(StudentView::class)
    var name: String? = null

    @JsonView(StudentView::class)
    var description: String? = null

    @Basic(optional = false)
    @JsonView(StudentView::class)
    var credit: Int? = null

    @Basic(optional = false)
    @JsonView(StudentView::class)
    var seats: Int? = null

    @Basic(optional = false)
    @JsonView(StudentView::class)
    var startDt: Instant? = null

    @JsonView(StudentView::class)
    var endDt: Instant? = null

    @Basic(optional = false)
    @JsonView(TeacherView::class)
    var published: Boolean = false

    @JsonView(StudentView::class)
    var coverImagePath: String? = null

    @OneToMany(mappedBy = "subject", fetch = FetchType.LAZY)
    @JsonView(StudentView::class)
    @JsonManagedReference
    @OrderBy("order")
    var lessons: List<Lesson> = emptyList()

    @JsonView(TeacherView::class)
    var archive: Boolean = false

    @Transient
    @get:JsonGetter
    @JsonView(TeacherView::class)
    var occupiedSeats: Int? = null
}

@Entity
@Table(name = "lesson", indexes = [Index(columnList = "tenantId")])
@Where(clause = "archive = false")
class Lesson : TenantAwareBaseEntity() {
    @Basic(optional = false)
    @JsonView(StudentView::class)
    var name: String? = null

    @JsonView(StudentView::class)
    var description: String? = null

    @Basic(optional = false)
    @JsonView(StudentView::class)
    var videoId: String? = null

    @Basic(optional = false)
    @JsonView(StudentView::class)
    var quizId: String? = null

    @JsonView(StudentView::class)
    var deadLine: Instant? = null

    @Basic(optional = false)
    @Column(name = "lesson_order")
    @JsonView(StudentView::class)
    var order: Int? = null

    @Basic(optional = false)
    @JsonView(TeacherView::class)
    var archive: Boolean? = false

    @ManyToOne
    @JsonBackReference
    // @JsonView(TransientView::class)
    var subject: Subject? = null
}
