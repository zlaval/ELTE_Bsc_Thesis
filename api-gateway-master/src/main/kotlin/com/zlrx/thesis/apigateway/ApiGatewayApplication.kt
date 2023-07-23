package com.zlrx.thesis.apigateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties
@ConfigurationPropertiesScan
@SpringBootApplication
class ApiGatewayApplication
fun main(args: Array<String>) {
    System.setProperty("user.timezone", "UTC");
    runApplication<ApiGatewayApplication>(*args)
}
