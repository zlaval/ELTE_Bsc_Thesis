package com.zlrx.thesis.subjectservice.controller.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import org.jetbrains.annotations.NotNull
import java.time.Instant

data class SubjectSaveRequest(

    @get:NotNull
    @get:NotBlank
    val name: String,

    val description: String?,

    @get:Positive
    val credit: Int,

    @get:Positive
    val seats: Int,

    @get:NotNull
    val startDt: Instant,

    val endDt: Instant?,
    val coverImagePath: String?
)

data class LessonRequest(
    val id: String?,

    @get:NotNull
    @get:NotBlank
    val name: String,

    @get:NotNull
    @get:NotBlank
    val description: String,

    @get:NotNull
    @get:NotBlank
    val quizId: String,

    @get:NotNull
    @get:NotBlank
    val videoId: String,

    val deadLine: Instant?
)
