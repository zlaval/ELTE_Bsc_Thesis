package com.zlrx.thesis.enrollmentservice.config

import com.mongodb.client.MongoClient
import io.mongock.driver.mongodb.sync.v4.driver.MongoSync4Driver
import io.mongock.runner.springboot.MongockSpringboot
import io.mongock.runner.springboot.base.MongockInitializingBeanRunner
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.EnableMongoAuditing

const val SYSTEM = "system"

@Configuration
@EnableMongoAuditing
class MongoConfig {

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
