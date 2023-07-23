package com.zlrx.thesis.subjectservice.service

import com.zlrx.thesis.subjectservice.api.enrollment.EnrollmentIntegration
import com.zlrx.thesis.subjectservice.api.grade.GradeIntegration
import com.zlrx.thesis.subjectservice.api.quiz.QuizIntegration
import com.zlrx.thesis.subjectservice.config.ApiException
import com.zlrx.thesis.subjectservice.config.Messages
import com.zlrx.thesis.subjectservice.controller.model.ActiveLesson
import com.zlrx.thesis.subjectservice.controller.model.ActiveSubjectLessons
import com.zlrx.thesis.subjectservice.domain.Subject
import com.zlrx.thesis.subjectservice.repository.SubjectRepository
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class StudentSubjectService(
    private val gradeIntegration: GradeIntegration,
    private val repository: SubjectRepository,
    private val enrollmentIntegration: EnrollmentIntegration,
    private val quizIntegration: QuizIntegration
) : BaseService() {

    fun getMySubjects(status: List<String>?): List<Subject> {
        val enrollments = enrollmentIntegration.myEnrollments(
            getUserId(),
            status
        )
        val sort = Sort.by(Sort.Direction.ASC, "startDt")
        return repository.findAllByIdIn(enrollments, sort)
    }

    fun findAllById(ids: List<String>): List<Subject> = repository.findAllById(ids)

    fun loadAvailableSubjects(): List<Subject> {
        val enrollments = enrollmentIntegration.myEnrollments(
            studentId = getUserId(),
            statusNotIn = listOf("IN_PROGRESS", "ENROLLED", "FINISHED")
        )
        return repository.findAvailableWithoutEnrolled(enrollments)
    }

    fun findAvailable(subjectId: String) = repository.findAvailableForEnrollById(subjectId)

    fun findEnrolledWithGrades(id: String): ActiveSubjectLessons {
        val subject = findById(id)
        val gradeData = gradeIntegration
            .getStudentAssessmentsForSubject(subject.id, getUserId()).associateBy { it.lessonId }

        val solutionData = quizIntegration.getSolvedQuizzes(subject.lessons.map { it.id })
            .associateBy { it.lessonId }

        return ActiveSubjectLessons(
            name = subject.name ?: "",
            description = subject.description,
            lessons = subject.lessons.map {
                ActiveLesson(
                    id = it.id,
                    name = it.name ?: "",
                    description = it.description,
                    finished = gradeData.containsKey(it.id) || solutionData.containsKey(it.id),
                    points = gradeData[it.id]?.points,
                    maxPoint = gradeData[it.id]?.maxPoints,
                    deadLine = it.deadLine
                )
            }
        )
    }

    fun findById(id: String): Subject {
        return repository.findByIdAndPublishedTrue(id) ?: throw ApiException(
            Messages.SUBJECT_NOT_FOUND,
            HttpStatus.NOT_FOUND,
            id
        )
    }
}
