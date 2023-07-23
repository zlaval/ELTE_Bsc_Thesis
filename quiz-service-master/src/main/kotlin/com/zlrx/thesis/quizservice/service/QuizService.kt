package com.zlrx.thesis.quizservice.service

import com.zlrx.thesis.quizservice.controller.model.EditQuizRequest
import com.zlrx.thesis.quizservice.domain.Answer
import com.zlrx.thesis.quizservice.domain.Question
import com.zlrx.thesis.quizservice.domain.Quiz
import com.zlrx.thesis.quizservice.domain.newId
import com.zlrx.thesis.quizservice.repository.QuizRepository
import com.zlrx.thesis.quizservice.repository.findByIdAndCreatedByOrThrow
import com.zlrx.thesis.quizservice.repository.findByIdOrThrow
import org.springframework.stereotype.Service

@Service
class QuizService(
    private val repository: QuizRepository
) : BaseService() {

    fun findById(id: String): Quiz {
        return if (getUser().isTeacher()) {
            repository.findByIdAndCreatedByOrThrow(id, getUserId())
        } else {
            repository.findByIdOrThrow(id)
        }
    }

    fun save(editQuiz: EditQuizRequest): Quiz {
        val quiz = mapToQuiz(editQuiz)
        return repository.save(quiz)
    }

    fun update(id: String, editQuiz: EditQuizRequest): Quiz {
        val quiz = repository.findByIdAndCreatedByOrThrow(id, getUserId())
        val updated = mapToQuiz(quiz, editQuiz)
        return repository.save(updated)
    }

    fun loadMyQuizzes(): List<Quiz> {
        return repository.findAllMyActive(getUserId())
    }

    fun archive(id: String) {
        val quiz = repository.findMyActiveById(id, getUserId())
        if (quiz != null) {
            repository.save(quiz.copy(archive = true))
        }
    }

    private fun mapToQuiz(edited: EditQuizRequest) = Quiz(
        id = newId(),
        name = edited.name,
        description = edited.description,
        questions = mapQuestions(edited)
    )

    private fun mapToQuiz(quiz: Quiz, edited: EditQuizRequest): Quiz = quiz.copy(
        name = edited.name,
        description = edited.description,
        questions = mapQuestions(edited)
    )

    private fun mapQuestions(editQuiz: EditQuizRequest): List<Question> = editQuiz.questions.map { eq ->
        Question(
            id = eq.id ?: newId(),
            question = eq.question,
            points = eq.points,
            answers = eq.answers.map { ea ->
                Answer(
                    id = ea.id ?: newId(),
                    text = ea.text,
                    accepted = ea.accepted
                )
            }
        )
    }
}
