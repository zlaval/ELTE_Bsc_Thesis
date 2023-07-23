package com.zlrx.thesis.subjectservice.stream

import com.zlrx.thesis.subjectservice.config.GRADE_QUEUE_NAME
import com.zlrx.thesis.subjectservice.config.SOLUTION_QUEUE_NAME
import com.zlrx.thesis.subjectservice.config.withAuthContext
import com.zlrx.thesis.subjectservice.domain.Lesson
import com.zlrx.thesis.subjectservice.service.StudentSubjectService
import com.zlrx.thesis.subjectservice.service.TeacherLessonService
import mu.KotlinLogging
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class SolutionQueueListener(
    private val lessonService: TeacherLessonService,
    private val subjectService: StudentSubjectService,
    private val rabbitTemplate: RabbitTemplate
) {

    private val logger = KotlinLogging.logger {}

    @RabbitListener(queues = [SOLUTION_QUEUE_NAME])
    fun listen(solution: SolutionModel) {
        logger.info { solution }

        withAuthContext(solution.tenantId) {
            val lesson = lessonService.findLesson(solution.lessonId)
            if (lesson != null) {
                if (isAvailable(lesson)) {
                    val subject = subjectService.findAvailable(lesson.subject!!.id)
                    if (subject != null) {
                        val lessonCount = lessonService.lessonCountForSubject(subject)
                        val message = EnhancedSolutionModel(
                            solution = solution,
                            subjectId = subject.id,
                            lessonCount = lessonCount.toInt(),
                            credit = subject.credit ?: 0,
                            subjectName = subject.name ?: ""
                        )
                        rabbitTemplate.convertAndSend(GRADE_QUEUE_NAME, message)
                    }
                } else {
                    logger.info { "The lesson ${lesson.id} deadline has ended" }
                }
            } else {
                logger.info { "Lesson not found ${solution.lessonId}" }
            }
        }
    }

    private fun isAvailable(lesson: Lesson): Boolean {
        return lesson.deadLine == null || lesson.deadLine?.isBefore(Instant.now()) == false
    }
}

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
