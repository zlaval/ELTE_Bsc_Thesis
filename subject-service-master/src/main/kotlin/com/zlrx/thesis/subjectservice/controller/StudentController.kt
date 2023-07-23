package com.zlrx.thesis.subjectservice.controller

import com.zlrx.thesis.subjectservice.controller.model.ActiveSubjectLessons
import com.zlrx.thesis.subjectservice.domain.Lesson
import com.zlrx.thesis.subjectservice.domain.Subject
import com.zlrx.thesis.subjectservice.service.StudentLessonService
import com.zlrx.thesis.subjectservice.service.StudentSubjectService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@PreAuthorize("hasRole('STUDENT')")
@RequestMapping("/api/v1/student")
class StudentController(
    private val subjectService: StudentSubjectService,
    private val lessonService: StudentLessonService
) {

    @GetMapping
    fun getMySubjects(@RequestParam status: List<String>?): ResponseEntity<List<Subject>> {
        val response = subjectService.getMySubjects(status)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/available")
    fun getAvailableSubjects(): ResponseEntity<List<Subject>> {
        val response = subjectService.loadAvailableSubjects()
        return ResponseEntity.ok(response)
    }

    @GetMapping("/enrolled/{id}")
    fun getEnrolledSubject(
        @PathVariable id: String
    ): ResponseEntity<ActiveSubjectLessons> {
        val response = subjectService.findEnrolledWithGrades(id)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getSubject(
        @PathVariable id: String
    ): ResponseEntity<Subject> {
        val response = subjectService.findById(id)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/lesson/{id}")
    fun getLesson(
        @PathVariable id: String
    ): ResponseEntity<Lesson> {
        val response = lessonService.findLesson(id)
        return ResponseEntity.ok(response)
    }
}
