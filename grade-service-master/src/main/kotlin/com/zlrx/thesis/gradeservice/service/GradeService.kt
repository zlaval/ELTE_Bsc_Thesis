package com.zlrx.thesis.gradeservice.service

import com.zlrx.thesis.gradeservice.api.auth.AuthIntegration
import com.zlrx.thesis.gradeservice.api.subject.SubjectIntegration
import com.zlrx.thesis.gradeservice.config.GRADE_EVENT
import com.zlrx.thesis.gradeservice.config.MessageResolver
import com.zlrx.thesis.gradeservice.config.NOTIFICATION_QUEUE_NAME
import com.zlrx.thesis.gradeservice.controller.model.GradeResponse
import com.zlrx.thesis.gradeservice.controller.model.StudentGrade
import com.zlrx.thesis.gradeservice.domain.Grade
import com.zlrx.thesis.gradeservice.domain.newId
import com.zlrx.thesis.gradeservice.repository.GradeRepository
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GradeService(
    private val repository: GradeRepository,
    private val authIntegration: AuthIntegration,
    private val subjectIntegration: SubjectIntegration,
    private val rabbitTemplate: RabbitTemplate,
    private val messageResolver: MessageResolver,
    @Autowired(required = false)
    userProvider: InjectableUserProvider = InjectableUserProviderImpl
) : BaseService(userProvider) {

    fun getMyGrades(): List<GradeResponse> {
        val grades = repository.findAllByUserId(getUserId())
        if (grades.isEmpty()) {
            return emptyList()
        }
        val deletedSubjectName = messageResolver.getMessage("deleted.subject")
        val subjectIds = grades.map { it.subjectId }
        val subjects = getSubjectsForGrades(subjectIds)

        return grades.map {
            val subjectName = subjects[it.subjectId]?.name ?: deletedSubjectName
            toGradeResponse(it, subjectName)
        }
    }

    private fun getSubjectsForGrades(subjectIds: List<String>) = if (subjectIds.isNotEmpty()) {
        subjectIntegration.getSubjects(subjectIds)
            .associateBy { it.id }
    } else {
        emptyMap()
    }

    private fun toGradeResponse(grade: Grade, subjectName: String) = GradeResponse(
        id = grade.id,
        subject = subjectName,
        credit = grade.credit,
        points = grade.points,
        mark = grade.mark
    )

    fun getGradesBySubjectId(subjectId: String): List<StudentGrade> {
        val grades = repository.findAllBySubjectId(subjectId, getTenantId())
        val userIds = grades.map { it.userId }
        val users = authIntegration.getUsers(userIds).associateBy {
            it.id
        }
        val deletedUserName = messageResolver.getMessage("deleted.user")

        return grades.map {
            val user = users[it.userId]
            StudentGrade(
                studentName = user?.name ?: deletedUserName,
                email = user?.email ?: "***",
                mark = it.mark,
                points = it.points
            )
        }
    }

    fun gradeStudent(
        studentId: String,
        subjectId: String,
        credit: Int,
        sumPoints: Int,
        maxPoints: Int,
        subjectName: String
    ) {

        val mark = calculateGrade(maxPoints, sumPoints)
        val finalCredit = if (mark == 1) 0 else credit
        val grade = Grade(
            newId(),
            subjectId,
            studentId,
            mark,
            sumPoints,
            finalCredit
        )

        val entity = repository.save(grade)
        sendGradeEvent(entity)
        notifyStudent(entity, subjectName)
    }

    private fun sendGradeEvent(grade: Grade) {
        rabbitTemplate.convertAndSend(GRADE_EVENT, grade)
    }

    private fun notifyStudent(grade: Grade, subjectName: String) {
        rabbitTemplate.convertAndSend(
            NOTIFICATION_QUEUE_NAME,
            NotificationMessage(
                title = messageResolver.getMessage("msg.grade.title", arrayOf(subjectName)),
                text = messageResolver.getMessage("msg.grade.body", arrayOf(grade.mark)),
                userId = grade.userId
            )
        )
    }

    private fun calculateGrade(maxPoints: Int, studentPoints: Int): Int {
        val percent = (studentPoints.toFloat() / maxPoints.toFloat()) * 100f
        return if (percent < 45)
            1
        else if (percent < 60)
            2
        else if (percent < 70)
            3
        else if (percent < 85)
            4
        else 5
    }
}

data class NotificationMessage(
    val title: String,
    val text: String,
    val userId: String
)
