package com.zlrx.thesis.enrollmentservice.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

const val ENROLLMENT_EXCHANGE_NAME = "enrollment-exchange"
const val ENROLLMENT_OUT_QUEUE_NAME = "enrollment-to-subject-queue"
const val ENROLLMENT_IN_QUEUE_NAME = "subject-to-enrollment-queue"

const val GRADE_EXCHANGE_NAME = "grade-exchange"
const val GRADE_EVENT = "grade_event_topic"

const val SUBJECT_EXCHANGE_NAME = "subject-exchange"
const val SUBJECT_CLOSED_EVENT_NAME = "subject-closed"

const val NOTIFICATION_EXCHANGE_NAME = "notification-exchange"
const val NOTIFICATION_QUEUE_NAME = "notification-queue"

@Configuration
class RabbitConfiguration {

    @Bean
    fun messageConverter(om: ObjectMapper): MessageConverter = Jackson2JsonMessageConverter(om)

    @Bean
    fun enrollmentExchange(): TopicExchange = TopicExchange(ENROLLMENT_EXCHANGE_NAME)

    @Bean
    fun enrollmentOutQueue(): Queue = Queue(ENROLLMENT_OUT_QUEUE_NAME, true)

    @Bean
    fun enrollmentInQueue(): Queue = Queue(ENROLLMENT_IN_QUEUE_NAME, true)

    @Bean
    fun enrollmentOutBinding(): Binding = BindingBuilder.bind(enrollmentOutQueue()).to(enrollmentExchange()).with(
        ENROLLMENT_OUT_QUEUE_NAME
    )

    @Bean
    fun enrollmentInBinding(): Binding = BindingBuilder.bind(enrollmentInQueue()).to(enrollmentExchange()).with(
        ENROLLMENT_IN_QUEUE_NAME
    )

    @Bean
    fun gradeExchange(): TopicExchange = TopicExchange(GRADE_EXCHANGE_NAME)

    @Bean
    fun gradeEventTopic(): Queue = Queue(GRADE_EVENT, true)

    @Bean
    fun gradeEventBinding(): Binding = BindingBuilder.bind(gradeEventTopic()).to(gradeExchange()).with(
        GRADE_EVENT
    )

    @Bean
    fun subjectExchange(): TopicExchange = TopicExchange(SUBJECT_EXCHANGE_NAME)

    @Bean
    fun subjectClosedQueue(): Queue = Queue(SUBJECT_CLOSED_EVENT_NAME, true)

    @Bean
    fun subjectClosedBinding(): Binding = BindingBuilder.bind(subjectClosedQueue()).to(subjectExchange()).with(
        SUBJECT_CLOSED_EVENT_NAME
    )

    @Bean
    fun notificationExchange(): TopicExchange = TopicExchange(NOTIFICATION_EXCHANGE_NAME)

    @Bean
    fun notificationQueue(): Queue = Queue(NOTIFICATION_QUEUE_NAME, true)

    @Bean
    fun solutionBinding(): Binding = BindingBuilder.bind(notificationQueue()).to(notificationExchange()).with(
        NOTIFICATION_QUEUE_NAME
    )
}
