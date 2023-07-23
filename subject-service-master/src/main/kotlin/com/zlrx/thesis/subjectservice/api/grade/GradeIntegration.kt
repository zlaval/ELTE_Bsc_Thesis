package com.zlrx.thesis.subjectservice.api.grade

import com.zlrx.thesis.subjectservice.config.FeignConfig
import com.zlrx.thesis.subjectservice.controller.model.Grade
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import mu.KotlinLogging
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

data class StudentFinishedLesson(
    val studentId: String,
    val count: Int
)

data class Assessment(
    val subjectId: String,
    val lessonId: String,
    val studentId: String,
    val points: Int,
    val maxPoints: Int,
)

@FeignClient(
    name = "grade-api",
    url = "\${knowhere.service.grade}/internal/api/v1",
    configuration = [FeignConfig::class]
)
interface GradeApi {

    @RequestMapping(method = [RequestMethod.GET], value = ["/progress/{subjectId}"])
    fun getStudentProgress(
        @PathVariable subjectId: String
    ): List<StudentFinishedLesson>

    @RequestMapping(method = [RequestMethod.GET], value = ["/grades/{subjectId}"])
    fun getAllGradesForSubject(
        @PathVariable subjectId: String
    ): List<Grade>

    @RequestMapping(method = [RequestMethod.GET], value = ["/assessments/{subjectId}/{userId}"])
    fun getStudentAssessmentsForSubject(
        @PathVariable subjectId: String,
        @PathVariable userId: String
    ): List<Assessment>

    @RequestMapping(method = [RequestMethod.GET], value = ["/assessment/{lessonId}/{studentId}"])
    fun isLessonFinishedByStudent(
        @PathVariable lessonId: String,
        @PathVariable studentId: String
    ): Boolean
}

@Component
class GradeIntegration(
    private val api: GradeApi
) {

    private val logger = KotlinLogging.logger {}

    @CircuitBreaker(name = "grade", fallbackMethod = "isLessonFinishedByStudentFallback")
    fun isLessonFinishedByStudent(lessonId: String, studentId: String): Boolean =
        api.isLessonFinishedByStudent(lessonId, studentId)

    @CircuitBreaker(name = "grade", fallbackMethod = "getStudentsProgressForSubjectFallback")
    fun getStudentsProgressForSubject(subjectId: String): Map<String, Int> {
        return api.getStudentProgress(subjectId).associate { Pair(it.studentId, it.count) }
    }

    @CircuitBreaker(name = "grade", fallbackMethod = "getSubjectGradesFallback")
    fun getSubjectGrades(subjectId: String): List<Grade> = api.getAllGradesForSubject(subjectId)

    @CircuitBreaker(name = "grade", fallbackMethod = "getStudentAssessmentsForSubjectFallback")
    fun getStudentAssessmentsForSubject(subjectId: String, userId: String): List<Assessment> =
        api.getStudentAssessmentsForSubject(subjectId, userId)

    fun getStudentAssessmentsForSubjectFallback(subjectId: String, userId: String, e: Exception): List<Assessment> {
        logger.error { "CircuitBreaker getStudentAssessmentsForSubjectFallback was called. ${e.message}" }
        return emptyList()
    }

    fun getSubjectGradesFallback(subjectId: String, e: Exception): List<Grade> {
        logger.error { "CircuitBreaker getSubjectGradesFallback was called. ${e.message}" }
        return emptyList()
    }

    fun getStudentsProgressForSubjectFallback(subjectId: String, e: Exception): Map<String, Int> {
        logger.error { "CircuitBreaker getStudentsProgressForSubjectFallback was called. ${e.message}" }
        return emptyMap()
    }

    fun isLessonFinishedByStudentFallback(lessonId: String, studentId: String, e: Exception): Boolean {
        logger.error { "CircuitBreaker isLessonFinishedByStudentFallback was called. ${e.message}" }
        return true
    }
}
