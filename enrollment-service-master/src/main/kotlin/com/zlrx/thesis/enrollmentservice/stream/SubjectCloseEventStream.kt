package com.zlrx.thesis.enrollmentservice.stream

import com.zlrx.thesis.enrollmentservice.config.SUBJECT_CLOSED_EVENT_NAME
import com.zlrx.thesis.enrollmentservice.config.withAuthContext
import com.zlrx.thesis.enrollmentservice.domain.EnrollmentStatus
import com.zlrx.thesis.enrollmentservice.repository.EnrollmentRepository
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class SubjectCloseEventStream(
    private val repository: EnrollmentRepository,
    private val notifier: StudentNotifier
) {

    @RabbitListener(queues = [SUBJECT_CLOSED_EVENT_NAME])
    fun enrollSubject(message: SubjectCloseMessage) {
        withAuthContext(message.tenantId) {
            val enrollments = repository.findEnrolledBySubjectId(message.id)
            val updated = enrollments.map {
                it.copy(status = EnrollmentStatus.CLOSED)
            }
            val result = repository.saveAll(updated)

            result.forEach {
                notifier.sendNotification(
                    it.studentId, message.name,
                    "message.subject.closed.title",
                    "message.subject.closed"
                )
            }
        }
    }
}

data class SubjectCloseMessage(
    val id: String,
    val name: String,
    val tenantId: String
)
