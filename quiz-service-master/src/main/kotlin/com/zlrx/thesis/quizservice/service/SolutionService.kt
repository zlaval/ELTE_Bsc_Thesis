package com.zlrx.thesis.quizservice.service

import com.zlrx.thesis.quizservice.controller.model.StudentSolutions
import com.zlrx.thesis.quizservice.domain.ProcessStatus
import com.zlrx.thesis.quizservice.domain.QuestionAnswer
import com.zlrx.thesis.quizservice.domain.Quiz
import com.zlrx.thesis.quizservice.domain.Solution
import com.zlrx.thesis.quizservice.domain.newId
import com.zlrx.thesis.quizservice.repository.QuizRepository
import com.zlrx.thesis.quizservice.repository.SolutionRepository
import com.zlrx.thesis.quizservice.repository.findByIdOrThrow
import com.zlrx.thesis.quizservice.stream.SolutionModel
import com.zlrx.thesis.quizservice.stream.SolutionPublisher
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class SolutionService(
    private val repository: SolutionRepository,
    private val quizRepository: QuizRepository,
    private val publisher: SolutionPublisher
) : BaseService() {

    fun solveQuiz(solutions: StudentSolutions) {
        val result = repository.save(
            Solution(
                id = newId(),
                quizId = solutions.quizId,
                lessonId = solutions.lessonId,
                studentId = getUserId(),
                answers = solutions.answers,
                lastAttempt = Instant.now()
            )
        )

        evaluateAndSend(result)
    }

    fun evaluateAndSend(solution: Solution) {
        val quiz = quizRepository.findByIdOrThrow(solution.quizId)
        val maxPoint = quiz.questions.sumOf { it.points }
        val points = calculatePoints(quiz, solution.answers)

        val message = SolutionModel(
            solutionId = solution.id,
            studentId = solution.studentId,
            quizId = solution.quizId,
            lessonId = solution.lessonId,
            tenantId = solution.tenantId,
            maxPoints = maxPoint,
            points = points
        )

        publisher.sendSolution(message)
        repository.save(solution.copy(status = ProcessStatus.SENT))
    }

    private fun calculatePoints(quiz: Quiz, answers: List<QuestionAnswer>): Int {
        val questionsMap = quiz.questions.associateBy { it.id }
        return answers.sumOf {
            val question = questionsMap[it.questionId]!!
            val rightAnswer = question.answers.first { a ->
                a.accepted
            }
            if (it.answerId == rightAnswer.id) {
                question.points
            } else {
                0
            }
        }
    }

    fun getStudentSolution(
        quizId: String,
        lessonId: String,
    ): Solution? {
        return repository.findSolvedForStudent(quizId, lessonId, getUserId(), getTenantId())
    }

    fun getStudentSolutions(lessonIds: List<String>): List<Solution> {
        return repository.findAllByLessonIdsForStudent(lessonIds, getUserId(), getTenantId())
    }
}
