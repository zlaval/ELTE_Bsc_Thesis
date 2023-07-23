package com.zlrx.thesis.gradeservice.repository

import com.zlrx.thesis.gradeservice.domain.Grade
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface GradeRepository : MongoRepository<Grade, String> {

    @Query(value = "{'subjectId':  ?0, 'tenantId':  ?1}")
    fun findAllBySubjectId(subjectId: String, tenantId: String): List<Grade>

    fun findAllByUserId(userId: String): List<Grade>
}
