package com.zlrx.thesis.subjectservice.service

import com.zlrx.thesis.subjectservice.api.enrollment.EnrollmentIntegration
import com.zlrx.thesis.subjectservice.api.grade.GradeIntegration
import com.zlrx.thesis.subjectservice.api.quiz.QuizIntegration
import com.zlrx.thesis.subjectservice.config.ApiException
import com.zlrx.thesis.subjectservice.config.Messages
import com.zlrx.thesis.subjectservice.config.Messages.LESSON_COMPLETED
import com.zlrx.thesis.subjectservice.config.Messages.LESSON_NOT_AVAILABLE
import com.zlrx.thesis.subjectservice.config.Messages.SUBJECT_COMPLETED
import com.zlrx.thesis.subjectservice.config.Messages.SUBJECT_NOT_AVAILABLE
import com.zlrx.thesis.subjectservice.config.Messages.SUBJECT_NOT_ENROLLED
import com.zlrx.thesis.subjectservice.domain.Lesson
import com.zlrx.thesis.subjectservice.repository.LessonRepository
import com.zlrx.thesis.subjectservice.repository.SubjectRepository
import com.zlrx.thesis.subjectservice.repository.findByIdOrThrow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
@Transactional
class StudentLessonService(
    private val repository: LessonRepository,
    private val subjectRepository: SubjectRepository,
    private val enrollmentIntegration: EnrollmentIntegration,
    private val gradeIntegration: GradeIntegration,
    private val quizIntegration: QuizIntegration
) : BaseService() {

    fun findLesson(id: String): Lesson {
        val lesson = repository.findByIdOrThrow(id)
        checkAvailability(lesson)
        return lesson
    }

    private fun checkAvailability(lesson: Lesson) {
        val studentId = getUserId()
        check(LESSON_NOT_AVAILABLE) {
            lesson.deadLine != null && lesson.deadLine?.isBefore(Instant.now()) != false
        }

        val subject = subjectRepository.findAvailableForEnrollById(lesson.subject!!.id)
        check(SUBJECT_NOT_AVAILABLE) {
            subject == null
        }

        check(SUBJECT_NOT_ENROLLED) {
            !enrollmentIntegration.isEnrolledByStudent(subject!!.id, studentId)
        }
        check(LESSON_COMPLETED) {
            quizIntegration.isQuizOfLessonSolved(lesson.quizId!!, lesson.id)
        }
        check(SUBJECT_COMPLETED) {
            gradeIntegration.isLessonFinishedByStudent(lesson.id, studentId)
        }
    }

    private inline fun check(message: Messages, predicate: () -> Boolean) {
        if (predicate()) {
            throw ApiException(message)
        }
    }
}
