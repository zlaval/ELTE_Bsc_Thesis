package com.zlrx.thesis.gradeservice.api.subject

import com.zlrx.thesis.gradeservice.config.FeignConfig
import com.zlrx.thesis.gradeservice.config.SUBJECT_INTEGRATION_CACHE
import io.github.resilience4j.bulkhead.annotation.Bulkhead
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import mu.KotlinLogging
import org.springframework.cache.annotation.Cacheable
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

data class Subject(
    val id: String,
    val name: String
) {
    companion object
}

@FeignClient(
    name = "subject-api",
    url = "\${knowhere.service.subject}/internal/api/v1",
    configuration = [FeignConfig::class]
)
interface SubjectApi {

    @RequestMapping(method = [RequestMethod.GET], value = [""])
    fun getSubjects(
        @RequestParam subjectIds: List<String>
    ): List<Subject>
}

@Component
class SubjectIntegration(
    private val api: SubjectApi
) {

    private val logger = KotlinLogging.logger {}

    @Cacheable(value = [SUBJECT_INTEGRATION_CACHE], key = "#ids")
    @Bulkhead(name = "subject", fallbackMethod = "getSubjectsFallback")
    @RateLimiter(name = "subject", fallbackMethod = "getSubjectsFallback")
    @CircuitBreaker(name = "subject", fallbackMethod = "getSubjectsFallback")
    fun getSubjects(ids: List<String>): List<Subject> = api.getSubjects(ids)

    fun getSubjectsFallback(ids: List<String>, e: Exception): List<Subject> {
        logger.error { "CircuitBreaker getSubjectsFallback was called. ${e.message}" }
        return emptyList()
    }
}
