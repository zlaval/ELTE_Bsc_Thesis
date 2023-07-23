package com.zlrx.thesis.authservice.config

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.stereotype.Component
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

enum class Messages(
    val code: String
) {
    ERROR_TITLE("common.error.title"),
    ERROR_DETAIL("common.error.detail"),
    VALIDATION_ERROR_TITLE("validation.error.title"),
    VALIDATION_ERROR_DETAIL("validation.error.detail"),
    VALIDATION_DEFAULT_MESSAGE("validation.default.message"),

    INVALID_CREDENTIALS("invalid.credentials"),
    TENANT_NOT_FOUND("tenant.not.found"),
    USER_NOT_FOUND("user.not.found"),
    INVALID_CURRENT_PASSWORD("invalid.current.password")
}

@Component
class MessageResolver(
    private val messageSource: MessageSource
) {

    fun getMessage(message: Messages, params: Array<out String>?): String {
        return getMessage(message.code, params)
    }

    fun getMessage(templateName: String, params: Array<out Any>? = null): String {
        val replace = if (params == null || params.isEmpty()) {
            null
        } else {
            params
        }
        return messageSource.getMessage(templateName, replace, Loc.HU)
    }
}
