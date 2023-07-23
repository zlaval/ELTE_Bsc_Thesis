package com.zlrx.thesis.enrollmentservice.api

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.timelimiter.TimeLimiterConfig
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder
import org.springframework.cloud.client.circuitbreaker.Customizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class ResilienceConfig {

    @Bean
    fun defaultCustomizer(): Customizer<Resilience4JCircuitBreakerFactory> =
        Customizer { factory ->
            factory.configureDefault { id ->
                Resilience4JConfigBuilder(id)
                    .timeLimiterConfig(
                        TimeLimiterConfig.custom()
                            .timeoutDuration(Duration.ofSeconds(1)).build()
                    )
                    .circuitBreakerConfig(
                        CircuitBreakerConfig.custom()
                            .slidingWindowType(
                                CircuitBreakerConfig.SlidingWindowType.TIME_BASED
                            )
                            .slidingWindowSize(50)
                            .minimumNumberOfCalls(50)
                            .failureRateThreshold(80f)
                            .waitDurationInOpenState(Duration.ofSeconds(10))
                            .slowCallDurationThreshold(Duration.ofMillis(500))
                            .slowCallRateThreshold(95f)
                            .build()
                    )
                    .build()
            }
        }
}
