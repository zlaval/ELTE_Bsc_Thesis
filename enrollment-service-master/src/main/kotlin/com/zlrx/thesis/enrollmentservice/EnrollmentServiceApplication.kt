package com.zlrx.thesis.enrollmentservice

import io.mongock.runner.springboot.EnableMongock
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@EnableMongock
@ConfigurationPropertiesScan
@EnableConfigurationProperties
@EnableMongoRepositories
@EnableTransactionManagement
@SpringBootApplication
@EnableFeignClients
class EnrollmentServiceApplication

fun main(args: Array<String>) {
    System.setProperty("user.timezone", "UTC")
    runApplication<EnrollmentServiceApplication>(*args)
}
