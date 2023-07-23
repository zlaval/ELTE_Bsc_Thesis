package com.zlrx.thesis.quizservice.stream

import com.zlrx.thesis.quizservice.config.MessageResolver
import com.zlrx.thesis.quizservice.config.NOTIFICATION_QUEUE_NAME
import com.zlrx.thesis.quizservice.config.SOLUTION_QUEUE_NAME
import com.zlrx.thesis.quizservice.config.withAuthContext
import com.zlrx.thesis.quizservice.domain.ProcessStatus
import com.zlrx.thesis.quizservice.repository.QuizRepository
import com.zlrx.thesis.quizservice.repository.SolutionRepository
import com.zlrx.thesis.quizservice.service.SolutionService
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.concurrent.TimeUnit

@Configuration
class Outpost(
    private val service: SolutionService,
    private val repository: SolutionRepository,
    private val rabbitTemplate: RabbitTemplate,
    private val quizRepository: QuizRepository,
    private val messageResolver: MessageResolver
) {

    @SchedulerLock(name = "solution_send")
    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
    fun sendFailedSolutions() {
        val failedSolutions = repository.findForMqRetry()

        failedSolutions.forEach {

            withAuthContext(it.tenantId) {
                val status = if (it.attempts < 3) {
                    service.evaluateAndSend(it)
                    it.status
                } else {
                    val quiz = quizRepository.findOneById(it.quizId)
                    sendNotification(quiz.name, it.studentId)
                    ProcessStatus.FAILED
                }
                repository.save(
                    it.copy(
                        lastAttempt = Instant.now(),
                        attempts = it.attempts + 1,
                        status = status
                    )
                )
            }
        }
    }

    private fun sendNotification(name: String, studentId: String) {
        rabbitTemplate.convertAndSend(
            NOTIFICATION_QUEUE_NAME,
            getNotificationMessage(name, studentId)
        )
    }

    private fun getNotificationMessage(name: String, studentId: String) = NotificationMessage(
        title = messageResolver.getMessage("solution.evaluation.failed.title"),
        text = messageResolver.getMessage("solution.evaluation.failed", arrayOf(name)),
        userId = studentId
    )
}

@Component
class SolutionPublisher(
    private val rabbitTemplate: RabbitTemplate
) {

    fun sendSolution(solution: SolutionModel) {
        rabbitTemplate.convertAndSend(SOLUTION_QUEUE_NAME, solution)
    }
}

data class SolutionModel(
    val solutionId: String,
    val studentId: String,
    val quizId: String,
    val lessonId: String,
    val points: Int,
    val maxPoints: Int,
    val tenantId: String
)

data class NotificationMessage(
    val title: String,
    val text: String,
    val userId: String
)
