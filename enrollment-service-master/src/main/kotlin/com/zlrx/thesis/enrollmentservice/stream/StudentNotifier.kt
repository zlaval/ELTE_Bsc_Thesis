package com.zlrx.thesis.enrollmentservice.stream

import com.zlrx.thesis.enrollmentservice.config.MessageResolver
import com.zlrx.thesis.enrollmentservice.config.NOTIFICATION_QUEUE_NAME
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class StudentNotifier(
    private val rabbitTemplate: RabbitTemplate,
    private val messageResolver: MessageResolver
) {

    fun sendNotification(
        studentId: String,
        subjectName: String,
        headerTemplate: String,
        bodyTemplate: String
    ) {
        val message = NotificationMessage(
            title = messageResolver.getMessage(headerTemplate),
            text = messageResolver.getMessage(
                bodyTemplate,
                arrayOf(subjectName)
            ),
            userId = studentId
        )
        rabbitTemplate.convertAndSend(NOTIFICATION_QUEUE_NAME, message)
    }
}
