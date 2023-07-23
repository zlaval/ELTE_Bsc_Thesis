package com.zlrx.thesis.subjectservice.controller

import com.zlrx.thesis.subjectservice.controller.model.LessonRequest
import com.zlrx.thesis.subjectservice.controller.model.StudentProgress
import com.zlrx.thesis.subjectservice.controller.model.SubjectForSelect
import com.zlrx.thesis.subjectservice.controller.model.SubjectSaveRequest
import com.zlrx.thesis.subjectservice.controller.model.SubjectWithGrades
import com.zlrx.thesis.subjectservice.domain.Lesson
import com.zlrx.thesis.subjectservice.domain.Subject
import com.zlrx.thesis.subjectservice.service.TeacherLessonService
import com.zlrx.thesis.subjectservice.service.TeacherSubjectService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@PreAuthorize("hasRole('TEACHER')")
@RequestMapping("/api/v1/teacher")
class TeacherController(
    private val subjectService: TeacherSubjectService,
    private val lessonService: TeacherLessonService,
) {

    @GetMapping
    fun getMySubjects(
        @RequestParam(name = "archive", defaultValue = "false")
        includeArchive: Boolean,
        @RequestParam(name = "page", defaultValue = "0")
        pageNumber: Int
    ): ResponseEntity<Page<Subject>> {
        val page = PageRequest.of(pageNumber, 8)
        val result = subjectService.findAll(includeArchive, page)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/select")
    fun getMySubjectsForSelect(): List<SubjectForSelect> {
        return subjectService.findAllMySubject().map {
            SubjectForSelect(it.id, it.name!!)
        }
    }

    @GetMapping("/subject-grades/{id}")
    fun getSubjectWithGrades(
        @PathVariable id: String
    ): ResponseEntity<SubjectWithGrades> {
        val response = subjectService.subjectWithGrades(id)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}/students")
    fun getStudentsForSubject(
        @PathVariable id: String
    ): ResponseEntity<List<StudentProgress>> {
        val response = subjectService.getStudentsForSubject(id)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getSubject(@PathVariable id: String): ResponseEntity<Subject> {
        val response = subjectService.findById(id)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{id}/publish")
    fun publish(@PathVariable id: String): ResponseEntity<Void> {
        subjectService.publish(id)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/image", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadImage(
        @RequestPart("coverImage")
        image: MultipartFile?
    ): ResponseEntity<String> {
        val imagePath = subjectService.saveFile(image)
        return ResponseEntity.ok(imagePath)
    }

    @PostMapping
    fun save(
        @Valid
        @RequestBody
        subject: SubjectSaveRequest,
    ): ResponseEntity<Subject> {
        val response = subjectService.saveSubject(subject)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable
        id: String,
        @Valid
        @RequestBody
        subject: SubjectSaveRequest,
    ): ResponseEntity<Subject> {
        val response = subjectService.updateSubject(id, subject)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{id}/archive")
    fun archive(
        @PathVariable id: String
    ): ResponseEntity<Void> {
        subjectService.archive(id, true)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{id}/unarchive")
    fun unarchive(
        @PathVariable id: String
    ): ResponseEntity<Subject> {
        val response = subjectService.archive(id, false)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{subjectId}/lesson/{id}")
    fun getLessonOfSubject(
        @PathVariable subjectId: String,
        @PathVariable id: String
    ): ResponseEntity<Lesson> {
        val result = lessonService.findLessonByIdForTeacher(id)
        return ResponseEntity.ok(result)
    }

    @PutMapping("/{subjectId}/lesson/{id}")
    fun updateLesson(
        @PathVariable subjectId: String,
        @PathVariable id: String,
        @RequestBody @Valid lesson: LessonRequest
    ): ResponseEntity<Lesson> {
        val response = lessonService.updateLesson(id, subjectId, lesson)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{subjectId}/lesson")
    fun saveLesson(
        @PathVariable subjectId: String,
        @RequestBody @Valid lesson: LessonRequest
    ): ResponseEntity<Lesson> {
        val response = lessonService.saveLesson(subjectId, lesson)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{subjectId}/lesson/{id}")
    fun archiveLesson(
        @PathVariable subjectId: String,
        @PathVariable id: String
    ): ResponseEntity<Void> {
        lessonService.archiveLesson(id)
        return ResponseEntity.noContent().build()
    }
}
