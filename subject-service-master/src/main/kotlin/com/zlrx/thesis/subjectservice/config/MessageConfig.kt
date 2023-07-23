package com.zlrx.thesis.subjectservice.config

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

    LESSON_NOT_FOUND("lesson.not_found"),
    LESSON_NOT_AVAILABLE("lesson.not_available"),
    LESSON_COMPLETED("lesson.completed"),

    SUBJECT_NOT_FOUND("subject.not_found"),
    SUBJECT_NOT_AVAILABLE("subject.not_available"),
    SUBJECT_NOT_ENROLLED("subject.not.enrolled"),
    SUBJECT_COMPLETED("subject.completed"),
    SUBJECT_NO_CLASS("subject.no.class"),
}

@Component
class MessageResolver(
    private val messageSource: MessageSource
) {

    fun getMessage(message: Messages, params: Array<out String>?): String {
        return getMessage(message.code, params)
    }

    fun getMessage(template: String, params: Array<out String>? = null): String {
        val replace = if (params == null || params.isEmpty()) {
            null
        } else {
            params
        }
        return messageSource.getMessage(template, replace, Loc.HU)
    }
}
