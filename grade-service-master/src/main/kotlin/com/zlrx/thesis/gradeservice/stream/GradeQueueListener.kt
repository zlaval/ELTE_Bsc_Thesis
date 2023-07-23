package com.zlrx.thesis.gradeservice.stream

import com.zlrx.thesis.gradeservice.config.GRADE_QUEUE_NAME
import com.zlrx.thesis.gradeservice.config.GRADE_QUIZ_QUEUE
import com.zlrx.thesis.gradeservice.config.MessageResolver
import com.zlrx.thesis.gradeservice.config.NOTIFICATION_QUEUE_NAME
import com.zlrx.thesis.gradeservice.config.withAuthContext
import com.zlrx.thesis.gradeservice.domain.Assessment
import com.zlrx.thesis.gradeservice.domain.newId
import com.zlrx.thesis.gradeservice.service.AssessmentService
import com.zlrx.thesis.gradeservice.service.GradeService
import com.zlrx.thesis.gradeservice.service.NotificationMessage
import mu.KotlinLogging
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class GradeQueueListener(
    private val assessmentService: AssessmentService,
    private val gradeService: GradeService,
    private val rabbitTemplate: RabbitTemplate,
    private val messageResolver: MessageResolver,
) {

    private val logger = KotlinLogging.logger {}

    @RabbitListener(queues = [GRADE_QUEUE_NAME])
    fun listen(enhanced: EnhancedSolutionModel) {
        val solution = enhanced.solution
        logger.info { "Processing solution ${solution.lessonId}/${solution.studentId}" }

        withAuthContext(solution.tenantId) {
            val studentId = solution.studentId
            val lessonId = solution.lessonId
            val assessment = assessmentService.getAssessmentForStudentByLessonId(lessonId, studentId)

            if (assessment == null) {
                saveAssessment(enhanced.subjectId, solution)
                sendAssessmentNotification(enhanced.subjectName, solution)
                gradeStudent(enhanced)
            } else {
                logger.info { "Student $studentId has already finished the lesson $lessonId" }
            }
            notifyGradeService(solution)
        }
    }

    private fun notifyGradeService(solution: SolutionModel) {
        rabbitTemplate.convertAndSend(
            GRADE_QUIZ_QUEUE,
            StatusReport(solution.solutionId, solution.tenantId)
        )
    }

    private fun gradeStudent(enhanced: EnhancedSolutionModel) {
        val studentId = enhanced.solution.studentId
        val assessments = assessmentService.getStudentAssessmentsForSubject(
            enhanced.subjectId,
            studentId
        )
        if (assessments.size == enhanced.lessonCount) {
            val sumPoints = assessments.sumOf { it.points }
            val sumMaxPoints = assessments.sumOf { it.maxPoints }
            gradeService.gradeStudent(
                studentId,
                enhanced.subjectId,
                enhanced.credit,
                sumPoints,
                sumMaxPoints,
                enhanced.subjectName
            )
        }
    }

    private fun sendAssessmentNotification(subjectName: String, solution: SolutionModel) {
        rabbitTemplate.convertAndSend(
            NOTIFICATION_QUEUE_NAME,
            NotificationMessage(
                title = messageResolver.getMessage("msg.assessment.title"),
                text = messageResolver.getMessage(
                    "msg.assessment.body",
                    arrayOf(solution.points, solution.maxPoints, subjectName)
                ),
                userId = solution.studentId
            )
        )
    }

    private fun saveAssessment(subjectId: String, solution: SolutionModel) {
        val newAssessment = Assessment(
            id = newId(),
            subjectId = subjectId,
            lessonId = solution.lessonId,
            studentId = solution.studentId,
            points = solution.points,
            maxPoints = solution.maxPoints,
            solutionId = solution.solutionId
        )
        assessmentService.saveAssessment(newAssessment)
    }
}

data class StatusReport(
    val solutionId: String,
    val tenantId: String
)

data class EnhancedSolutionModel(
    val solution: SolutionModel,
    val subjectId: String,
    val lessonCount: Int,
    val credit: Int,
    val subjectName: String
)

data class SolutionModel(
    val solutionId: String,
    val studentId: String,
    val quizId: String,
    val lessonId: String,
    val points: Int,
    val maxPoints: Int,
    val tenantId: String
)
