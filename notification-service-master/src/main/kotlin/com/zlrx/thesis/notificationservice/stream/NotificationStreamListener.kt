package com.zlrx.thesis.notificationservice.stream

import com.zlrx.thesis.notificationservice.config.NOTIFICATION_QUEUE_NAME
import com.zlrx.thesis.notificationservice.domain.NotificationEntity
import com.zlrx.thesis.notificationservice.domain.Status
import com.zlrx.thesis.notificationservice.domain.newId
import com.zlrx.thesis.notificationservice.repository.NotificationRepository
import mu.KotlinLogging
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class NotificationStreamListener(
    private val repository: NotificationRepository
) {

    private val logger = KotlinLogging.logger {}

    @RabbitListener(queues = [NOTIFICATION_QUEUE_NAME])
    fun listener(message: NotificationMessage) {
        logger.info { "Message arrived to user ${message.userId}" }
        repository.save(
            NotificationEntity(
                id = newId(),
                userId = message.userId,
                title = message.title,
                text = message.text,
                status = Status.NEW
            )
        )
    }
}

data class NotificationMessage(
    val title: String,
    val text: String,
    val userId: String
)
