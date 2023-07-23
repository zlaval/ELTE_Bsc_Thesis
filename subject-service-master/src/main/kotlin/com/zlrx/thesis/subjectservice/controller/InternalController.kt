package com.zlrx.thesis.subjectservice.controller

import com.zlrx.thesis.subjectservice.domain.Subject
import com.zlrx.thesis.subjectservice.service.StudentSubjectService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("internal/api/v1")
class InternalController(
    private val service: StudentSubjectService
) {

    @GetMapping
    fun getSubjects(@RequestParam subjectIds: List<String>): List<Subject> {
        return service.findAllById(subjectIds)
    }
}
