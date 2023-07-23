package com.zlrx.thesis.enrollmentservice.stream

import com.zlrx.thesis.enrollmentservice.config.GRADE_EVENT
import com.zlrx.thesis.enrollmentservice.config.withAuthContext
import com.zlrx.thesis.enrollmentservice.domain.EnrollmentStatus
import com.zlrx.thesis.enrollmentservice.repository.EnrollmentRepository
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class GradeEventStream(
    private val repository: EnrollmentRepository
) {
    @RabbitListener(queues = [GRADE_EVENT])
    fun enrollSubject(message: GradeMessage) {
        withAuthContext(message.tenantId) {
            val enrollment = repository.findEnrolledBySubjectIdAndStudentId(message.subjectId, message.userId)
            if (enrollment != null) {
                val updated = enrollment.copy(status = EnrollmentStatus.FINISHED)
                repository.save(updated)
            }
        }
    }
}

data class GradeMessage(
    val subjectId: String,
    val userId: String,
    val tenantId: String
)
