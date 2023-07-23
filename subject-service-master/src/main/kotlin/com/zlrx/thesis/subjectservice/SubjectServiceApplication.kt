package com.zlrx.thesis.subjectservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@ConfigurationPropertiesScan
@EnableConfigurationProperties
@EnableJpaRepositories
@EnableTransactionManagement
@SpringBootApplication
@EnableFeignClients
class SubjectServiceApplication

fun main(args: Array<String>) {
    System.setProperty("user.timezone", "UTC")
    runApplication<SubjectServiceApplication>(*args)
}
