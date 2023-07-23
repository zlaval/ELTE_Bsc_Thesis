package com.zlrx.thesis.quizservice.controller.model

import com.zlrx.thesis.quizservice.domain.Question
import com.zlrx.thesis.quizservice.domain.Quiz

data class QuizResponse(
    val id: String,
    val name: String,
    val description: String? = null,
    val questions: List<Question> = emptyList(),
) {
    companion object
}

fun QuizResponse.Companion.fromQuiz(quiz: Quiz) = QuizResponse(
    id = quiz.id,
    name = quiz.name,
    description = quiz.description,
    questions = quiz.questions
)
