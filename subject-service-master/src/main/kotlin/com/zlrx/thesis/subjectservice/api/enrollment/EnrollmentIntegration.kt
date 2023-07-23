package com.zlrx.thesis.subjectservice.api.enrollment

import com.zlrx.thesis.subjectservice.config.FeignConfig
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import mu.KotlinLogging
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

data class CountResponse(
    val count: Int
)

data class User(
    val id: String,
    val name: String,
    val email: String
)

data class SubjectEnrollers(
    val subjectId: String,
    val students: List<User>
)

@FeignClient(
    name = "enrollment-api",
    url = "\${knowhere.service.enrollment}/internal/api/v1",
    configuration = [FeignConfig::class]
)
interface EnrollmentApi {

    @RequestMapping(method = [RequestMethod.GET], value = ["/student-count/{subjectId}"])
    fun studentsCountForSubject(@PathVariable("subjectId") subjectId: String): CountResponse

    @RequestMapping(method = [RequestMethod.GET], value = ["/students/{subjectId}"])
    fun studentsForSubject(
        @PathVariable("subjectId") subjectId: String
    ): SubjectEnrollers

    @RequestMapping(method = [RequestMethod.GET], value = ["/student-enrollments/{studentId}"])
    fun enrollmentOfStudent(
        @PathVariable studentId: String,
        @RequestParam
        status: List<String>?
    ): List<String>

    @RequestMapping(method = [RequestMethod.GET], value = ["/enrolled/{subjectId}/{studentId}"])
    fun isSubjectEnrolledByStudent(
        @PathVariable subjectId: String,
        @PathVariable studentId: String
    ): Boolean
}

@Component
open class EnrollmentIntegration(
    private val api: EnrollmentApi
) {

    private val logger = KotlinLogging.logger {}

    @CircuitBreaker(name = "enrollment", fallbackMethod = "isEnrolledByStudentFallback")
    fun isEnrolledByStudent(subjectId: String, studentId: String): Boolean =
        api.isSubjectEnrolledByStudent(subjectId, studentId)

    fun isEnrolledByStudentFallback(subjectId: String, studentId: String, e: Exception): Boolean {
        logger.error { "CircuitBreaker isEnrolledByStudentFallback was called. ${e.message}" }
        return false
    }

    @CircuitBreaker(name = "enrollment", fallbackMethod = "enrolledByFallback")
    fun enrolledBy(subjectId: String): List<User> {
        return api.studentsForSubject(subjectId).students
    }

    fun enrolledByFallback(subjectId: String, e: Exception): List<User> {
        logger.error { "CircuitBreaker enrolledByFallback was called. ${e.message}" }
        return emptyList()
    }

    @CircuitBreaker(name = "enrollment", fallbackMethod = "enrollCountFallback")
    fun enrollCount(subjectId: String): Int? = api.studentsCountForSubject(subjectId).count

    fun enrollCountFallback(subjectId: String, e: Exception): Int? {
        logger.error { "CircuitBreaker enrollCountFallback was called. ${e.message}" }
        return null
    }

    @CircuitBreaker(name = "enrollment", fallbackMethod = "myEnrollmentsFallback")
    fun myEnrollments(studentId: String, statusNotIn: List<String>? = null): List<String> {
        return api.enrollmentOfStudent(studentId, statusNotIn)
    }

    fun myEnrollmentsFallback(studentId: String, status: List<String>?, e: Exception): List<String> {
        logger.error { "CircuitBreaker myEnrollmentsFallback was called. ${e.message}" }
        return emptyList()
    }
}
