package com.zlrx.thesis.gradeservice.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

const val GRADE_EXCHANGE_NAME = "grade-exchange"
const val GRADE_QUEUE_NAME = "grade_queue"
const val GRADE_EVENT = "grade_event_topic"
const val GRADE_DLQ = "grade_dlq"

const val NOTIFICATION_EXCHANGE_NAME = "notification-exchange"
const val NOTIFICATION_QUEUE_NAME = "notification-queue"

const val GRADE_QUIZ_EXCHANGE = "grade-quiz-exchange"
const val GRADE_QUIZ_QUEUE = "grade-quiz-queue"

@Configuration
class RabbitConfiguration {

    @Bean
    fun messageConverter(om: ObjectMapper): MessageConverter = Jackson2JsonMessageConverter(om)

    @Bean
    fun gradeExchange(): TopicExchange = TopicExchange(GRADE_EXCHANGE_NAME)

    @Bean
    fun gradeQueue(): Queue = QueueBuilder.durable(GRADE_QUEUE_NAME)
        // .withArgument("x-dead-letter-exchange", "")
        // .withArgument("x-dead-letter-routing-key", GRADE_DLQ)
        .build()

    @Bean
    fun gradeBinding(): Binding = BindingBuilder.bind(gradeQueue()).to(gradeExchange()).with(
        GRADE_QUEUE_NAME
    )

    @Bean
    fun gradeDLQ(): Queue = QueueBuilder.durable(GRADE_DLQ).build()

    @Bean
    fun gradeEventTopic(): Queue = Queue(GRADE_EVENT, true)

    @Bean
    fun gradeEventBinding(): Binding = BindingBuilder.bind(gradeEventTopic()).to(gradeExchange()).with(
        GRADE_EVENT
    )

    @Bean
    fun notificationExchange(): TopicExchange = TopicExchange(NOTIFICATION_EXCHANGE_NAME)

    @Bean
    fun notificationQueue(): Queue = Queue(NOTIFICATION_QUEUE_NAME, true)

    @Bean
    fun notificationBinding(): Binding = BindingBuilder.bind(notificationQueue()).to(notificationExchange()).with(
        NOTIFICATION_QUEUE_NAME
    )

    @Bean
    fun gradeQuizExchange(): TopicExchange = TopicExchange(GRADE_QUIZ_EXCHANGE)

    @Bean
    fun gradeQuizQueue(): Queue = Queue(GRADE_QUIZ_QUEUE, true)

    @Bean
    fun gradeQuizBinding(): Binding = BindingBuilder.bind(gradeQuizQueue()).to(gradeQuizExchange()).with(
        GRADE_QUIZ_QUEUE
    )
}
