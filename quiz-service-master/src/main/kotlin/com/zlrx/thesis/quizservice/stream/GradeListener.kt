package com.zlrx.thesis.quizservice.stream

import com.zlrx.thesis.quizservice.config.GRADE_QUIZ_QUEUE
import com.zlrx.thesis.quizservice.config.withAuthContext
import com.zlrx.thesis.quizservice.domain.ProcessStatus
import com.zlrx.thesis.quizservice.repository.SolutionRepository
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class GradeListener(
    private val repository: SolutionRepository
) {

    @RabbitListener(queues = [GRADE_QUIZ_QUEUE])
    fun listen(message: StatusReport) {
        withAuthContext(message.tenantId) {
            val solution = repository.findById(message.solutionId)
            solution.ifPresent {
                repository.save(it.copy(status = ProcessStatus.FINISHED))
            }
        }
    }
}

data class StatusReport(
    val solutionId: String,
    val tenantId: String
)
