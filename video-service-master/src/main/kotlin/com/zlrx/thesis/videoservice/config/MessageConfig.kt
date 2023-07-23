package com.zlrx.thesis.videoservice.config

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import java.nio.charset.StandardCharsets
import java.util.Locale

object Loc {
    val HU = Locale("hu", "HU")
}

@Configuration
class MessageConfig {

    @Bean
    fun messageSource(): MessageSource {
        val messageSource = ResourceBundleMessageSource()
        messageSource.setBasename("language/messages")
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name())
        messageSource.setDefaultLocale(Loc.HU)
        messageSource.setUseCodeAsDefaultMessage(true)
        return messageSource
    }
}
