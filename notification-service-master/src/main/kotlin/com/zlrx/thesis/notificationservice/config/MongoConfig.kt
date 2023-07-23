package com.zlrx.thesis.notificationservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.mongodb.config.EnableMongoAuditing
import java.util.Optional

@Configuration
@EnableMongoAuditing
class MongoConfig {

    @Bean
    fun auditAwareProvider() = AuditorAware {
        Optional.ofNullable(authInfo()?.id)
            .or { Optional.of(SYSTEM) }
    }
}
