package com.zlrx.thesis.subjectservice.stream

import com.zlrx.thesis.subjectservice.config.ENROLLMENT_IN_QUEUE_NAME
import com.zlrx.thesis.subjectservice.config.ENROLLMENT_OUT_QUEUE_NAME
import com.zlrx.thesis.subjectservice.config.MessageResolver
import com.zlrx.thesis.subjectservice.config.withAuthContext
import com.zlrx.thesis.subjectservice.service.StudentSubjectService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class EnrollmentQueueListener(
    private val subjectService: StudentSubjectService,
    private val rabbitTemplate: RabbitTemplate,
    private val messageResolver: MessageResolver
) {

    @RabbitListener(queues = [ENROLLMENT_IN_QUEUE_NAME])
    fun listener(message: EnrollmentMessage) {
        withAuthContext(message.tenantId) {
            val subject = subjectService.findAvailable(message.subjectId)
            val response = if (subject == null) {
                getSubjectMessage(message, 0, false)
            } else {
                getSubjectMessage(message, subject.seats!!, true, subject.name)
            }
            rabbitTemplate.convertAndSend(ENROLLMENT_OUT_QUEUE_NAME, response)
        }
    }

    private fun getSubjectMessage(message: EnrollmentMessage, seats: Int, enrollable: Boolean, name: String? = null) =
        SubjectMessage(
            enrollmentId = message.enrollmentId,
            enrollable = enrollable,
            seats = seats,
            tenantId = message.tenantId,
            name = name ?: messageResolver.getMessage("subject.unknown")
        )
}

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
