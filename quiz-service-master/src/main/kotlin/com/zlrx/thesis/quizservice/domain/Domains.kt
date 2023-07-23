package com.zlrx.thesis.quizservice.domain

import com.chrylis.codec.base58.Base58UUID
import com.fasterxml.jackson.annotation.JsonView
import com.zlrx.thesis.quizservice.controller.view.TeacherView
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

const val SYSTEM = "system"
fun newId() = Base58UUID().encode(UUID.randomUUID())

abstract class TenantAwareEntity {
    @Indexed
    lateinit var tenantId: String
}

@Document(collection = "quiz")
data class Quiz(

    @Id
    val id: String,
    val name: String,
    val description: String? = null,
    val questions: List<Question> = emptyList(),
    val archive: Boolean = false,

    @Version
    var version: Long = 0,

    @CreatedDate
    var createdAt: Instant? = null,

    @LastModifiedDate
    var modifiedAt: Instant? = null,

    @CreatedBy
    @Indexed(sparse = true)
    var createdBy: String? = null,

    @LastModifiedBy
    var modifiedBy: String? = null,
) : TenantAwareEntity()

enum class ProcessStatus {
    NEW, SENT, FINISHED, FAILED
}

@Document(collection = "solution")
data class Solution(
    @Id
    val id: String,

    val quizId: String,

    val lessonId: String,

    val studentId: String,

    val answers: List<QuestionAnswer> = emptyList(),

    val status: ProcessStatus = ProcessStatus.NEW,

    val attempts: Int = 0,

    val lastAttempt: Instant,

    @CreatedDate
    var createdAt: Instant? = null,

    @CreatedBy
    var createdBy: String? = null
) : TenantAwareEntity()

data class QuestionAnswer(
    val questionId: String,
    val answerId: String,
)

data class Question(
    @Indexed(unique = true)
    val id: String,
    val question: String,
    val points: Int,
    val answers: List<Answer> = emptyList()
)

data class Answer(
    @Indexed(unique = true)
    val id: String,
    val text: String,
    @JsonView(TeacherView::class)
    val accepted: Boolean,
)
