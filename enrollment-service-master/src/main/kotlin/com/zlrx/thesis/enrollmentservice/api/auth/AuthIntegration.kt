package com.zlrx.thesis.enrollmentservice.api.auth

import com.zlrx.thesis.enrollmentservice.api.FeignConfig
import com.zlrx.thesis.enrollmentservice.config.AUTH_INTEGRATION_CACHE
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import mu.KotlinLogging
import org.springframework.cache.annotation.Cacheable
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

data class User(
    val id: String,
    val name: String,
    val email: String
)

@FeignClient(
    name = "auth-api",
    url = "\${knowhere.service.auth}/internal/api/v1",
    configuration = [FeignConfig::class]
)
interface AuthApi {

    @RequestMapping(method = [RequestMethod.GET], value = ["/users"])
    fun getUsers(@RequestParam userIds: List<String>): List<User>
}

@Component
class AuthIntegration(
    private val authApi: AuthApi
) {

    private val logger = KotlinLogging.logger {}

    @Cacheable(value = [AUTH_INTEGRATION_CACHE], key = "#userIds")
    @CircuitBreaker(name = "auth", fallbackMethod = "getUsersFallback")
    fun getUsers(userIds: List<String>): List<User> {
        return authApi.getUsers(userIds)
    }

    fun getUsersFallback(userIds: List<String>, e: Exception): List<User> {
        logger.error { "CircuitBreaker getUsersFallback was called. ${e.message}" }
        return emptyList()
    }
}
