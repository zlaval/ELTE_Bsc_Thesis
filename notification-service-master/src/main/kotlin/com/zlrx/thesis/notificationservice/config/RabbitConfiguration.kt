package com.zlrx.thesis.notificationservice.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

const val NOTIFICATION_EXCHANGE_NAME = "notification-exchange"
const val NOTIFICATION_QUEUE_NAME = "notification-queue"

@Configuration
class RabbitConfiguration {

    @Bean
    fun messageConverter(om: ObjectMapper): MessageConverter = Jackson2JsonMessageConverter(om)

    @Bean
    fun notificationExchange(): TopicExchange = TopicExchange(NOTIFICATION_EXCHANGE_NAME)

    @Bean
    fun notificationQueue(): Queue = Queue(NOTIFICATION_QUEUE_NAME, true)

    @Bean
    fun notificationBinding(): Binding = BindingBuilder.bind(notificationQueue()).to(notificationExchange()).with(
        NOTIFICATION_QUEUE_NAME
    )
}
