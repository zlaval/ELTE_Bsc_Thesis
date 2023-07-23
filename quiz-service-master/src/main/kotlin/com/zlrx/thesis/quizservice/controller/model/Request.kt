package com.zlrx.thesis.quizservice.controller.model

import com.zlrx.thesis.quizservice.controller.validation.ValidAnswer
import com.zlrx.thesis.quizservice.domain.QuestionAnswer
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import org.jetbrains.annotations.NotNull

data class EditQuizRequest(

    @get:NotNull
    @get:NotBlank
    val name: String,
    val description: String? = null,

    @get:Valid
    @get:Size(min = 1)
    @get:NotEmpty
    val questions: List<EditQuestion>
)

@ValidAnswer
data class EditQuestion(
    val id: String?,

    @get:NotNull
    @get:NotBlank
    val question: String,

    @get:NotNull
    @get:Positive
    val points: Int,

    @get:NotEmpty
    @get:Size(min = 2)
    @get:Valid
    val answers: List<EditAnswer>
)

data class EditAnswer(
    val id: String?,

    @get:NotNull
    @get:NotBlank
    val text: String,

    @get:NotNull
    val accepted: Boolean
)

data class StudentSolutions(
    @get:NotNull
    val lessonId: String,
    @get:NotNull
    val quizId: String,
    val answers: List<QuestionAnswer>
)
