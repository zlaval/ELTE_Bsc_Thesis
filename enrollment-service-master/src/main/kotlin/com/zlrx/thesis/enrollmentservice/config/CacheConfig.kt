package com.zlrx.thesis.enrollmentservice.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

const val AUTH_INTEGRATION_CACHE = "auth-integration"

@Configuration
class CacheConfig {

    @Bean
    fun authCache() = CaffeineCache(
        AUTH_INTEGRATION_CACHE,
        Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(5))
            .build()
    )
}
