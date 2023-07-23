package com.zlrx.thesis.enrollmentservice.repository

import com.zlrx.thesis.enrollmentservice.domain.Enrollment
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface EnrollmentRepository : MongoRepository<Enrollment, String> {

    fun findAllByStudentIdAndStatusIn(studentId: String, status: List<String>): List<Enrollment>

    @Query(value = "{'subjectId':  ?0, 'status': { \$in: ['ENROLLED','FINISHED','CLOSED'] }}")
    fun findSuccessfulForSubject(subjectId: String): List<Enrollment>

    @Query(value = "{'subjectId':  ?0, 'status': { \$in: ['ENROLLED','FINISHED','CLOSED'] }}", count = true)
    fun countSuccessfulBySubjectId(subjectId: String): Int

    @Query(value = "{'subjectId':  ?0, 'studentId': ?1, 'status': { \$in: ['ENROLLED','FINISHED','CLOSED'] }}")
    fun findSuccessfulBySubjectIdAndStudentId(subjectId: String, studentId: String): Enrollment?

    @Query(value = "{'subjectId':  ?0, 'status': 'ENROLLED'}")
    fun findEnrolledBySubjectId(subjectId: String): List<Enrollment>

    @Query(value = "{'subjectId':  ?0, 'studentId': ?1, 'status': 'ENROLLED'}")
    fun findEnrolledBySubjectIdAndStudentId(subjectId: String, studentId: String): Enrollment?
}
