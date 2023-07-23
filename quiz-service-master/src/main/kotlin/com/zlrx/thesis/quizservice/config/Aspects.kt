package com.zlrx.thesis.quizservice.config

import com.zlrx.thesis.quizservice.config.Messages.ERROR_DETAIL
import com.zlrx.thesis.quizservice.config.Messages.ERROR_TITLE
import com.zlrx.thesis.quizservice.config.Messages.VALIDATION_DEFAULT_MESSAGE
import com.zlrx.thesis.quizservice.config.Messages.VALIDATION_ERROR_DETAIL
import com.zlrx.thesis.quizservice.config.Messages.VALIDATION_ERROR_TITLE
import jakarta.validation.ConstraintViolation
import mu.KotlinLogging
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import org.springframework.validation.BindException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.URI

@Configuration
@EnableAspectJAutoProxy
class AopConfig

@Component
@Aspect
class RestControllerLoggingAop() {
    private val logger = KotlinLogging.logger {}

    @Pointcut(value = "@within(org.springframework.web.bind.annotation.RestController)")
    fun controllerMethods() {
    }

    @Before("controllerMethods()")
    fun logControllerMethodCall(jp: JoinPoint) {
        logger.info { "${jp.signature.declaringTypeName}.${jp.signature.name} called by ${authInfo()?.id ?: "unknown"}" }
    }
}

open class ApiException(
    val msg: Messages,
    val status: HttpStatus = HttpStatus.BAD_REQUEST,
    vararg val params: String,
    throwable: Throwable? = null
) : RuntimeException(throwable)

@ControllerAdvice
@RestControllerAdvice
class RestErrorHandler(
    private val messageResolver: MessageResolver
) {

    private val logger = KotlinLogging.logger {}

    @ExceptionHandler(BindException::class)
    fun handleValidationError(error: BindException): ResponseEntity<ProblemDetail> {
        val problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST)
        problemDetail.title = getMessage(VALIDATION_ERROR_TITLE)
        problemDetail.detail = getMessage(VALIDATION_ERROR_DETAIL)
        problemDetail.type = URI("/quiz")
        val errors = error.bindingResult.allErrors.associate {
            val message = it.defaultMessage ?: getMessage(VALIDATION_DEFAULT_MESSAGE)
            val field = if (it.contains(ConstraintViolation::class.java)) {
                val cv = it.unwrap(ConstraintViolation::class.java)
                val f = cv.propertyPath.toString()
                f.ifBlank { it.code }
            } else {
                it.code
            } ?: "Unknown field"
            Pair(field, message)
        }
        problemDetail.setProperty("invalid_fields", errors)

        return ResponseEntity.badRequest().body(problemDetail)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAuthorizationError(error: AccessDeniedException): ResponseEntity<Void> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
    }

    @ExceptionHandler(ApiException::class)
    fun handleApplicationError(error: ApiException): ResponseEntity<*> {
        val detail = ProblemDetail.forStatus(error.status).apply {
            title = getMessage(error.msg, error.params)
            type = URI("/quiz")
        }

        return ResponseEntity.status(error.status)
            .body(detail)
    }

    @ExceptionHandler(Exception::class)
    fun handleError(e: Exception): ResponseEntity<ProblemDetail> {
        logger.error(e.message, e)
        val problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        problemDetail.title = getMessage(ERROR_TITLE)
        problemDetail.detail = getMessage(ERROR_DETAIL)
        problemDetail.type = URI("/quiz")
        return ResponseEntity.badRequest().body(problemDetail)
    }

    private fun getMessage(msg: Messages, params: Array<out String>? = null) = messageResolver.getMessage(msg, params)
}
