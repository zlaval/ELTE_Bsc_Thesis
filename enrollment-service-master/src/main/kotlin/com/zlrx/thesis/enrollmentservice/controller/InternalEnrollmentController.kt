package com.zlrx.thesis.enrollmentservice.controller

import com.zlrx.thesis.enrollmentservice.controller.model.CountResponse
import com.zlrx.thesis.enrollmentservice.controller.model.SubjectEnrollers
import com.zlrx.thesis.enrollmentservice.service.EnrollmentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("internal/api/v1")
class InternalEnrollmentController(
    private val service: EnrollmentService
) {

    @GetMapping("/students/{subjectId}")
    fun getEnrolledStudentsForSubject(
        @PathVariable subjectId: String
    ): SubjectEnrollers {
        return service.studentsOnSubject(subjectId)
    }

    @GetMapping("/student-count/{subjectId}")
    fun getEnrollmentCountForSubject(
        @PathVariable subjectId: String
    ): ResponseEntity<CountResponse> {
        val response = service.enrollmentCountOnSubject(subjectId)
        return ResponseEntity.ok(CountResponse(response))
    }

    @GetMapping("/student-enrollments/{studentId}")
    fun getActiveEnrollmentsSubjectIdForStudent(
        @PathVariable studentId: String,
        @RequestParam status: List<String>?
    ): ResponseEntity<List<String>> {
        val response = service.studentEnrollments(studentId, status).map {
            it.subjectId
        }
        return ResponseEntity.ok(response)
    }

    @RequestMapping(method = [RequestMethod.GET], value = ["/enrolled/{subjectId}/{studentId}"])
    fun isSubjectEnrolledByStudent(
        @PathVariable subjectId: String,
        @PathVariable studentId: String
    ): ResponseEntity<Boolean> {
        return ResponseEntity.ok(service.isEnrolledByStudent(subjectId, studentId))
    }
}
