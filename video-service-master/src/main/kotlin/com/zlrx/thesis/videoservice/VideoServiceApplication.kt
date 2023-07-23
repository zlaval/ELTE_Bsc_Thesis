package com.zlrx.thesis.videoservice

import io.mongock.runner.springboot.EnableMongock
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.transaction.annotation.EnableTransactionManagement

@EnableAsync
@EnableMongock
@ConfigurationPropertiesScan
@EnableConfigurationProperties
@EnableMongoRepositories
@EnableTransactionManagement
@SpringBootApplication
class VideoServiceApplication

fun main(args: Array<String>) {
    System.setProperty("user.timezone", "UTC")
    runApplication<VideoServiceApplication>(*args)
}
