package com.zlrx.thesis.subjectservice.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

const val SOLUTION_EXCHANGE_NAME = "solution-exchange"
const val SOLUTION_QUEUE_NAME = "solution-queue"
const val GRADE_EXCHANGE_NAME = "grade-exchange"
const val GRADE_QUEUE_NAME = "grade_queue"

const val ENROLLMENT_EXCHANGE_NAME = "enrollment-exchange"
const val ENROLLMENT_IN_QUEUE_NAME = "enrollment-to-subject-queue"
const val ENROLLMENT_OUT_QUEUE_NAME = "subject-to-enrollment-queue"

const val SUBJECT_EXCHANGE_NAME = "subject-exchange"
const val SUBJECT_CLOSED_EVENT_NAME = "subject-closed"

@Configuration
class RabbitConfiguration {

    @Bean
    fun messageConverter(om: ObjectMapper): MessageConverter = Jackson2JsonMessageConverter(om)

    @Bean
    fun subjectExchange(): TopicExchange = TopicExchange(SUBJECT_EXCHANGE_NAME)

    @Bean
    fun subjectClosedQueue(): Queue = Queue(SUBJECT_CLOSED_EVENT_NAME, true)

    @Bean
    fun subjectClosedBinding(): Binding = BindingBuilder.bind(subjectClosedQueue()).to(subjectExchange()).with(
        SUBJECT_CLOSED_EVENT_NAME
    )

    @Bean
    fun solutionExchange(): TopicExchange = TopicExchange(SOLUTION_EXCHANGE_NAME)

    @Bean
    fun solutionQueue(): Queue = Queue(SOLUTION_QUEUE_NAME, true)

    @Bean
    fun solutionBinding(): Binding = BindingBuilder.bind(solutionQueue()).to(solutionExchange()).with(
        SOLUTION_QUEUE_NAME
    )

    @Bean
    fun gradeExchange(): TopicExchange = TopicExchange(GRADE_EXCHANGE_NAME)

    @Bean
    fun gradeQueue(): Queue = Queue(GRADE_QUEUE_NAME, true)

    @Bean
    fun gradeBinding(): Binding = BindingBuilder.bind(gradeQueue()).to(gradeExchange()).with(
        GRADE_QUEUE_NAME
    )

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
}
