package com.zlrx.thesis.subjectservice.repository

import com.zlrx.thesis.subjectservice.config.ApiException
import com.zlrx.thesis.subjectservice.config.Messages.SUBJECT_NOT_FOUND
import com.zlrx.thesis.subjectservice.config.authInfo
import com.zlrx.thesis.subjectservice.config.orBlow
import com.zlrx.thesis.subjectservice.domain.Subject
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

@Transactional(propagation = Propagation.MANDATORY)
interface SubjectRepository : JpaRepository<Subject, String>, CustomSubjectRepository {

    @EntityGraph(value = "Subject.noJoin")
    fun findAllBy(page: PageRequest): Page<Subject>

    @EntityGraph(value = "Subject.noJoin")
    fun findAllByArchiveFalse(page: PageRequest): Page<Subject>

    @EntityGraph(value = "Subject.noJoin")
    fun findAllByIdIn(ids: List<String>, sort: Sort): List<Subject>

    @EntityGraph(value = "Subject.lessons")
    fun findOneById(id: String): Subject?

    @EntityGraph(value = "Subject.lessons")
    fun findByIdAndPublishedTrue(id: String): Subject?
}

interface CustomSubjectRepository {
    fun findAvailableWithoutEnrolled(enrolledSubjectIds: List<String>): List<Subject>

    fun findAvailableForEnrollById(subjectIds: String): Subject?

    fun findExpiredInLastHour(): List<Subject>
}

class CustomSubjectRepositoryImpl(
    private val entityManager: EntityManager
) : CustomSubjectRepository {
    override fun findAvailableWithoutEnrolled(enrolledSubjectIds: List<String>): List<Subject> {
        val now = Instant.now().toString()
        val tenant = authInfo().orBlow().tenantId
        val baseSql = """
           select * from subject s 
             where s.published = true
                and s.archive = false
                and (s.end_dt >= cast('$now' as timestamp) or s.end_dt is null)  
                and s.tenant_id='$tenant'
        """.trimIndent()

        val sql = if (enrolledSubjectIds.isNotEmpty()) {
            val ids = enrolledSubjectIds.joinToString("','", "'", "'")
            "$baseSql and s.id not in ($ids)"
        } else {
            baseSql
        }

        val nativeQuery = entityManager.createNativeQuery(sql, Subject::class.java)
        return nativeQuery.resultList as List<Subject>
    }

    override fun findAvailableForEnrollById(subjectId: String): Subject? {
        val now = Instant.now().toString()
        val tenant = authInfo().orBlow().tenantId
        val sql = """
           select * from subject s 
             where s.published = true
                and s.archive = false
                and (s.end_dt >= cast(:now as timestamp) or s.end_dt is null)  
                and s.tenant_id=:tenant
                and s.id=:subjectId
        """.trimIndent()

        val nativeQuery = entityManager.createNativeQuery(sql, Subject::class.java)
        nativeQuery.setParameter("now", now)
        nativeQuery.setParameter("tenant", tenant)
        nativeQuery.setParameter("subjectId", subjectId)
        return nativeQuery.singleResult as Subject
    }

    override fun findExpiredInLastHour(): List<Subject> {
        val now = Instant.now().toString()
        val pastOneHour = Instant.now().minus(1, ChronoUnit.HOURS).toString()
        val sql = """ 
            select * from subject s 
                where s.end_dt < cast(:now as timestamp)
                and  s.end_dt> cast(:pastOneHour as timestamp)
            
        """.trimIndent()

        val nativeQuery = entityManager.createNativeQuery(sql, Subject::class.java)
        nativeQuery.setParameter("now", now)
        nativeQuery.setParameter("pastOneHour", pastOneHour)
        return nativeQuery.resultList as List<Subject>
    }
}

fun SubjectRepository.findByIdOrThrow(id: String): Subject =
    this.findById(id).orElseThrow { ApiException(SUBJECT_NOT_FOUND, NOT_FOUND, id) }
