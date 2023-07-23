package com.zlrx.thesis.subjectservice.api.quiz

import com.zlrx.thesis.subjectservice.config.FeignConfig
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import mu.KotlinLogging
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "quiz-api",
    url = "\${knowhere.service.quiz}/internal/api/v1",
    configuration = [FeignConfig::class]
)
interface QuizApi {

    @RequestMapping(method = [RequestMethod.GET], value = ["/solution/{quizId}/{lessonId}"])
    fun isQuizSolvedByStudent(
        @PathVariable quizId: String,
        @PathVariable lessonId: String,
    ): Boolean

    @RequestMapping(method = [RequestMethod.GET], value = ["/solution"])
    fun getSolvedQuizzesForLesson(
        @RequestParam lessonIds: List<String>,
    ): List<Solution>
}

data class Solution(
    val lessonId: String,
    val quizId: String
)

@Component
class QuizIntegration(
    private val api: QuizApi
) {

    private val logger = KotlinLogging.logger {}

    @CircuitBreaker(name = "quiz", fallbackMethod = "getSolvedQuizzesFallback")
    fun getSolvedQuizzes(lessonIds: List<String>): List<Solution> = api.getSolvedQuizzesForLesson(lessonIds)

    fun getSolvedQuizzesFallback(lessonIds: List<String>, e: Exception): List<Solution> {
        logger.error { "CircuitBreaker getSolvedQuizzesFallback was called. ${e.message}" }
        return emptyList()
    }

    @CircuitBreaker(name = "quiz", fallbackMethod = "isQuizOfLessonSolvedFallback")
    fun isQuizOfLessonSolved(quizId: String, lessonId: String): Boolean =
        api.isQuizSolvedByStudent(quizId, lessonId)

    fun isQuizOfLessonSolvedFallback(quizId: String, lessonId: String, e: Exception): Boolean {
        logger.error { "CircuitBreaker isQuizOfLessonSolvedFallback was called. ${e.message}" }
        return true
    }
}
