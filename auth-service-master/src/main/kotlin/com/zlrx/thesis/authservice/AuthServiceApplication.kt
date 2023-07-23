package com.zlrx.thesis.authservice

import io.mongock.runner.springboot.EnableMongock
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@EnableMongock
@ConfigurationPropertiesScan
@EnableConfigurationProperties
@EnableMongoRepositories
@SpringBootApplication
class AuthServiceApplication

fun main(args: Array<String>) {
    System.setProperty("user.timezone", "UTC")
    runApplication<AuthServiceApplication>(*args)
}
