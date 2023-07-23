package com.zlrx.thesis.springadmin

import de.codecentric.boot.admin.server.config.EnableAdminServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
@EnableAdminServer
@SpringBootApplication
class SpringAdminApplication

fun main(args: Array<String>) {
    runApplication<SpringAdminApplication>(*args)
}
