package com.zlrx.thesis.quizservice.controller.view

import com.zlrx.thesis.quizservice.config.Role
import com.zlrx.thesis.quizservice.config.authInfo
import com.zlrx.thesis.quizservice.config.orBlow
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJacksonValue
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.AbstractMappingJacksonResponseBodyAdvice

sealed class BasicView
open class StudentView : BasicView()
class TeacherView : StudentView()
object View {
    val mapping = mapOf(
        Role.ROLE_STUDENT to StudentView::class.java,
        Role.ROLE_TEACHER to TeacherView::class.java,
        "UNKNOWN" to BasicView::class.java
    )
}

@RestControllerAdvice
class ViewFromRole : AbstractMappingJacksonResponseBodyAdvice() {
    override fun beforeBodyWriteInternal(
        bodyContainer: MappingJacksonValue,
        contentType: MediaType,
        returnType: MethodParameter,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ) {
        val auth = authInfo()
        if (auth != null) {
            val role = auth.orBlow().role
            bodyContainer.serializationView = View.mapping[role]
        }
    }
}
