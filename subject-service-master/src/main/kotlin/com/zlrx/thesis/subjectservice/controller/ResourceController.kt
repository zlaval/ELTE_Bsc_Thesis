package com.zlrx.thesis.subjectservice.controller

import com.zlrx.thesis.subjectservice.service.TeacherSubjectService
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/resource")
class ResourceController(
    private val subjectService: TeacherSubjectService
) {

    @GetMapping("/image/{path}", produces = [MediaType.IMAGE_JPEG_VALUE])
    fun getCoverImage(
        @PathVariable path: String
    ): Resource {
        return subjectService.loadFile(path)
    }
}
