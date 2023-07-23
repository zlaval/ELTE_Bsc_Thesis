package com.zlrx.thesis.notificationservice.stream

import com.zlrx.thesis.notificationservice.WithTestContainers
import com.zlrx.thesis.notificationservice.awaitAssert
import com.zlrx.thesis.notificationservice.config.NOTIFICATION_QUEUE_NAME
import com.zlrx.thesis.notificationservice.repository.NotificationRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@WithTestContainers
@Disabled
class NotificationStreamListenerTest {

    @Autowired
    lateinit var rabbitTemplate: RabbitTemplate

    @Autowired
    lateinit var repository: NotificationRepository

    @BeforeEach
    fun init() {
        repository.deleteAll()
    }

    @Test
    fun `should save the message into the db`() {
        val message = NotificationMessage("Test title", "Test message body", "4")

        rabbitTemplate.convertAndSend(NOTIFICATION_QUEUE_NAME, message)

        awaitAssert {
            val notifications = repository.findAll()
            assertThat(notifications).hasSize(1)
            assertThat(notifications.first().title).isEqualTo("Test title")
            assertThat(notifications.first().text).isEqualTo("Test message body")
            assertThat(notifications.first().userId).isEqualTo("4")
        }
    }
}
