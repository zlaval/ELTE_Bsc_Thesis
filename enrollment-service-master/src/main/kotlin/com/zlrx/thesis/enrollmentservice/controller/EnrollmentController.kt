package com.zlrx.thesis.enrollmentservice.controller

import com.zlrx.thesis.enrollmentservice.service.EnrollmentService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class EnrollmentController(
    private val service: EnrollmentService
) {

    @PostMapping("/{subjectId}")
    @PreAuthorize("hasRole('STUDENT')")
    fun enroll(
        @PathVariable subjectId: String
    ) {
        return service.enroll(subjectId)
    }

    @DeleteMapping("/{subjectId}")
    @PreAuthorize("hasRole('STUDENT')")
    fun dropEnrollment(
        @PathVariable subjectId: String
    ) {
        return service.dropEnrollment(subjectId)
    }
}
