package com.zlrx.thesis.enrollmentservice.stream

import com.zlrx.thesis.enrollmentservice.config.ENROLLMENT_IN_QUEUE_NAME
import com.zlrx.thesis.enrollmentservice.config.withAuthContext
import com.zlrx.thesis.enrollmentservice.domain.Enrollment
import com.zlrx.thesis.enrollmentservice.domain.EnrollmentStatus
import com.zlrx.thesis.enrollmentservice.repository.EnrollmentRepository
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

data class SubjectMessage(
    val enrollmentId: String,
    val enrollable: Boolean,
    val seats: Int,
    val tenantId: String,
    val name: String
)

data class EnrollmentMessage(
    val enrollmentId: String,
    val subjectId: String,
    val tenantId: String
)

@Component
class EnrollmentStream(
    private val repository: EnrollmentRepository,
    private val notifier: StudentNotifier
) {

    @RabbitListener(queues = [ENROLLMENT_IN_QUEUE_NAME])
    fun enrollSubject(message: SubjectMessage) {

        withAuthContext(message.tenantId) {
            val enrollment: Enrollment? = repository.findByIdOrNull(message.enrollmentId)
            if (enrollment != null) {
                val enrollmentsCount = repository.countSuccessfulBySubjectId(enrollment.subjectId)

                val status = if (enrollmentsCount >= message.seats) {
                    EnrollmentStatus.NO_SEATS
                } else if (!message.enrollable) {
                    EnrollmentStatus.ERROR
                } else {
                    EnrollmentStatus.ENROLLED
                }

                sendNotificationByStatus(status, enrollment.studentId, message.name)

                repository.save(enrollment.copy(status = status))
            }
        }
    }

    fun sendNotificationByStatus(status: EnrollmentStatus, studentId: String, subjectName: String) {
        when (status) {
            EnrollmentStatus.ENROLLED -> notifier.sendNotification(
                studentId, subjectName,
                "message.enrollment.success.title",
                "message.enrollment.success"
            )

            EnrollmentStatus.ERROR -> notifier.sendNotification(
                studentId, subjectName,
                "message.enrollment.failed.title",
                "message.enrollment.subject.expired"
            )

            EnrollmentStatus.NO_SEATS -> notifier.sendNotification(
                studentId, subjectName,
                "message.enrollment.failed.title",
                "message.enrollment.subject.full"
            )

            else -> {}
        }
    }
}

data class NotificationMessage(
    val title: String,
    val text: String,
    val userId: String
)
