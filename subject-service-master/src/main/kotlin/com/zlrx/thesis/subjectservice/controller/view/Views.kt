package com.zlrx.thesis.subjectservice.controller.view

import com.zlrx.thesis.subjectservice.config.Role
import com.zlrx.thesis.subjectservice.config.authInfo
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJacksonValue
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.AbstractMappingJacksonResponseBodyAdvice

class TransientView

sealed class BasicView
open class StudentView : BasicView()
class TeacherView : StudentView()
object View {
    val mapping = mapOf(
        Role.ROLE_STUDENT to StudentView::class.java,
        Role.ROLE_TEACHER to TeacherView::class.java,
        "UNAUTHORIZED" to BasicView::class.java,
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
            val role = auth.role
            bodyContainer.serializationView = View.mapping[role]
        }
    }
}
