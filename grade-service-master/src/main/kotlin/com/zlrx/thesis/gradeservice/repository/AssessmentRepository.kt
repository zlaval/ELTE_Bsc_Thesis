package com.zlrx.thesis.gradeservice.repository

import com.zlrx.thesis.gradeservice.domain.Assessment
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface AssessmentRepository : MongoRepository<Assessment, String> {

    @Query(value = "{'subjectId':  ?0, 'tenantId':  ?1}")
    fun findAllBySubjectId(subjectId: String, tenantId: String): List<Assessment>

    @Query(value = "{'subjectId':  ?0, 'studentId':  ?1, 'tenantId':  ?2}")
    fun findAllBySubjectIdAndStudentId(subjectId: String, studentId: String, tenantId: String): List<Assessment>

    @Query(value = "{'lessonId':  ?0, 'studentId':  ?1, 'tenantId':  ?2}")
    fun findOneByLessonIdAndStudentId(lessonId: String, studentId: String, tenantId: String): Assessment?
}
