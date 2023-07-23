package com.zlrx.thesis.enrollmentservice.service

import com.zlrx.thesis.enrollmentservice.api.auth.AuthIntegration
import com.zlrx.thesis.enrollmentservice.config.ApiException
import com.zlrx.thesis.enrollmentservice.config.ENROLLMENT_OUT_QUEUE_NAME
import com.zlrx.thesis.enrollmentservice.config.Messages.ALREADY_ENROLLED
import com.zlrx.thesis.enrollmentservice.controller.model.SubjectEnrollers
import com.zlrx.thesis.enrollmentservice.domain.Enrollment
import com.zlrx.thesis.enrollmentservice.domain.EnrollmentStatus
import com.zlrx.thesis.enrollmentservice.domain.newId
import com.zlrx.thesis.enrollmentservice.repository.EnrollmentRepository
import com.zlrx.thesis.enrollmentservice.stream.EnrollmentMessage
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class EnrollmentService(
    private val repository: EnrollmentRepository,
    private val authIntegration: AuthIntegration,
    private val rabbitTemplate: RabbitTemplate
) : BaseService() {

    fun enroll(subjectId: String) {
        val user = getUser()

        val existingEnrollment = repository.findSuccessfulBySubjectIdAndStudentId(subjectId, user.id)
        if (existingEnrollment != null) {
            throw ApiException(ALREADY_ENROLLED)
        }
        val enrollment = Enrollment(
            id = newId(),
            subjectId = subjectId,
            studentId = user.id,
            status = EnrollmentStatus.IN_PROGRESS,
            tenantId = user.tenantId
        )
        repository.save(enrollment)
        rabbitTemplate.convertAndSend(
            ENROLLMENT_OUT_QUEUE_NAME,
            EnrollmentMessage(enrollment.id, subjectId, user.tenantId)
        )
    }

    fun dropEnrollment(subjectId: String) {
        val enrollment = repository.findEnrolledBySubjectIdAndStudentId(subjectId, getUserId())
        if (enrollment != null) {
            val updated = enrollment.copy(status = EnrollmentStatus.DROPPED)
            repository.save(updated)
        }
    }

    fun studentsOnSubject(subjectId: String): SubjectEnrollers {
        val enrollments = repository.findSuccessfulForSubject(subjectId)
        val userIds = enrollments.map { it.studentId }
        val users = if (userIds.isEmpty()) {
            emptyList()
        } else {
            authIntegration.getUsers(userIds)
        }

        return SubjectEnrollers(
            subjectId, users
        )
    }

    fun enrollmentCountOnSubject(subjectId: String) = repository.countSuccessfulBySubjectId(subjectId)

    fun studentEnrollments(studentId: String, status: List<String>?): List<Enrollment> {
        val statusFilter = status ?: listOf(EnrollmentStatus.ENROLLED.name)
        return repository.findAllByStudentIdAndStatusIn(studentId, statusFilter)
    }

    fun isEnrolledByStudent(subjectId: String, studentId: String): Boolean {
        return repository.findEnrolledBySubjectIdAndStudentId(subjectId, studentId) != null
    }
}
