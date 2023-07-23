package com.zlrx.thesis.enrollmentservice.api

import com.zlrx.thesis.enrollmentservice.config.authInfo
import feign.RequestInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders

class FeignConfig {

    @Bean
    fun requestInterceptor(): RequestInterceptor = RequestInterceptor {
        val auth = authInfo()
        if (auth != null) {
            it.header(HttpHeaders.AUTHORIZATION, "Bearer ${auth.token}")
        }
    }
}
