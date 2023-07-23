package com.zlrx.thesis.quizservice.config

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

const val GRADE_QUIZ_EXCHANGE = "grade-quiz-exchange"
const val GRADE_QUIZ_QUEUE = "grade-quiz-queue"

const val NOTIFICATION_EXCHANGE_NAME = "notification-exchange"
const val NOTIFICATION_QUEUE_NAME = "notification-queue"

@Configuration
class RabbitConfiguration {

    @Bean
    fun messageConverter(om: ObjectMapper): MessageConverter = Jackson2JsonMessageConverter(om)

    @Bean
    fun solutionExchange(): TopicExchange = TopicExchange(SOLUTION_EXCHANGE_NAME)

    @Bean
    fun solutionQueue(): Queue = Queue(SOLUTION_QUEUE_NAME, true)

    @Bean
    fun solutionBinding(): Binding = BindingBuilder.bind(solutionQueue()).to(solutionExchange()).with(
        SOLUTION_QUEUE_NAME
    )

    @Bean
    fun gradeQuizExchange(): TopicExchange = TopicExchange(GRADE_QUIZ_EXCHANGE)

    @Bean
    fun gradeQuizQueue(): Queue = Queue(GRADE_QUIZ_QUEUE, true)

    @Bean
    fun gradeQuizBinding(): Binding = BindingBuilder.bind(gradeQuizQueue()).to(gradeQuizExchange()).with(
        GRADE_QUIZ_QUEUE
    )

    @Bean
    fun notificationExchange(): TopicExchange = TopicExchange(NOTIFICATION_EXCHANGE_NAME)

    @Bean
    fun notificationQueue(): Queue = Queue(NOTIFICATION_QUEUE_NAME, true)

    @Bean
    fun notificationBinding(): Binding = BindingBuilder.bind(notificationQueue()).to(notificationExchange()).with(
        NOTIFICATION_QUEUE_NAME
    )
}
