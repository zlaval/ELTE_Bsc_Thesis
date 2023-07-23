package com.zlrx.thesis.gradeservice.service

import com.zlrx.thesis.gradeservice.domain.Assessment
import com.zlrx.thesis.gradeservice.repository.AssessmentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AssessmentService(
    private val repository: AssessmentRepository,
    @Autowired(required = false)
    userProvider: InjectableUserProvider = InjectableUserProviderImpl
) : BaseService(userProvider) {

    fun getStudentsProgressForSubject(subjectId: String): Map<String, Int> {
        val allProgress = repository.findAllBySubjectId(subjectId, getTenantId())
        return allProgress.groupBy { it.studentId }.mapValues { it.value.size }
    }

    fun getStudentAssessmentsForSubject(subjectId: String, studentId: String): List<Assessment> {
        return repository.findAllBySubjectIdAndStudentId(subjectId, studentId, getTenantId())
    }

    fun getAssessmentForStudentByLessonId(lessonId: String, studentId: String): Assessment? {
        return repository.findOneByLessonIdAndStudentId(lessonId, studentId, getTenantId())
    }

    fun saveAssessment(assessment: Assessment) = repository.save(assessment)
}
