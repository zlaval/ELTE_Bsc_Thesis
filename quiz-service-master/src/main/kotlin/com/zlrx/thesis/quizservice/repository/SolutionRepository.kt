package com.zlrx.thesis.quizservice.repository

import com.zlrx.thesis.quizservice.domain.ProcessStatus
import com.zlrx.thesis.quizservice.domain.Solution
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.time.Instant
import java.time.temporal.ChronoUnit
import org.springframework.data.mongodb.core.query.Query as MongoQuery

interface SolutionRepository : MongoRepository<Solution, String>, CustomSolutionRepository {

    @Query("{'quizId': ?0, 'lessonId': ?1,  'studentId': ?2 ,'tenantId':  ?3   }")
    fun findSolvedForStudent(
        quizId: String,
        lessonId: String,
        studentId: String,
        tenantId: String
    ): Solution?

    @Query("{'lessonId': {\$in: ?0},'studentId': ?1, 'tenantId':  ?2  }")
    fun findAllByLessonIdsForStudent(lessonId: List<String>, studentId: String, tenantId: String): List<Solution>
}

interface CustomSolutionRepository {

    fun findForMqRetry(): List<Solution>
}

class CustomSolutionRepositoryImpl(
    private val mongoTemplate: MongoTemplate
) : CustomSolutionRepository {
    override fun findForMqRetry(): List<Solution> {
        val before10Minutes = Instant.now().minus(10, ChronoUnit.MINUTES)

        val criteria = Criteria.where("attempts").lte(3)
            .and("status").`in`(listOf(ProcessStatus.NEW, ProcessStatus.SENT))
            .and("lastAttempt").lte(before10Minutes)

        return mongoTemplate.find(MongoQuery.query(criteria), Solution::class.java)
    }
}
