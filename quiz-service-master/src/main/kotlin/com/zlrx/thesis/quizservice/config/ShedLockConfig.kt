package com.zlrx.thesis.quizservice.config

import com.mongodb.client.MongoClient
import net.javacrumbs.shedlock.core.LockProvider
import net.javacrumbs.shedlock.provider.mongo.MongoLockProvider
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "30s")
@ConditionalOnProperty("knowhere.scheduling.enabled", havingValue = "true", matchIfMissing = false)
class ShedLockConfig {

    @Bean
    fun lockProvider(mongo: MongoClient): LockProvider = MongoLockProvider(
        mongo.getDatabase("quiz")
    )
}
