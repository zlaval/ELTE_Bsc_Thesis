package com.zlrx.thesis.gradeservice.config

import com.mongodb.client.MongoClient
import com.zlrx.thesis.gradeservice.domain.TenantAwareEntity
import io.mongock.driver.mongodb.sync.v4.driver.MongoSync4Driver
import io.mongock.runner.springboot.MongockSpringboot
import io.mongock.runner.springboot.base.MongockInitializingBeanRunner
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.core.TenantAwareMongoTemplate
import org.springframework.data.mongodb.core.convert.MongoConverter
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback
import org.springframework.stereotype.Component
import java.util.Optional

@Configuration
@EnableMongoAuditing
class MongoConfig {

    @Bean("mongoTemplate")
    fun mongoTemplate(mongoDbFactory: MongoDatabaseFactory, mongoConverter: MongoConverter) =
        TenantAwareMongoTemplate(mongoDbFactory, mongoConverter)

    @Bean
    fun auditAwareProvider() = AuditorAware {
        Optional.ofNullable(authInfo()?.id)
            .or { Optional.of(SYSTEM) }
    }

    @Bean
    fun builder(
        client: MongoClient,
        context: ApplicationContext,
        @Value("\${spring.data.mongodb.database}")
        database: String
    ): MongockInitializingBeanRunner =
        MongockSpringboot.builder()
            .setDriver(MongoSync4Driver.withDefaultLock(client, database))
            .addMigrationScanPackage("com.zlrx.thesis")
            .setSpringContext(context)
            .setTransactionEnabled(false)
            .buildInitializingBeanRunner()
}

@Component
class BeforeSaveListener : BeforeConvertCallback<TenantAwareEntity> {
    override fun onBeforeConvert(entity: TenantAwareEntity, collection: String): TenantAwareEntity =
        entity.apply { tenantId = authInfo().orBlow().tenantId }
}
