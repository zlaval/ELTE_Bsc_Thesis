package com.zlrx.thesis.gradeservice.controller

import com.zlrx.thesis.gradeservice.controller.model.StudentFinishedLesson
import com.zlrx.thesis.gradeservice.controller.model.StudentGrade
import com.zlrx.thesis.gradeservice.domain.Assessment
import com.zlrx.thesis.gradeservice.service.AssessmentService
import com.zlrx.thesis.gradeservice.service.GradeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("internal/api/v1")
class InternalController(
    private val assessmentService: AssessmentService,
    private val gradeService: GradeService
) {

    @GetMapping("/progress/{subjectId}")
    fun getProgressForSubject(
        @PathVariable subjectId: String
    ): ResponseEntity<List<StudentFinishedLesson>> {
        val response = assessmentService.getStudentsProgressForSubject(subjectId)
            .map { (student, count) ->
                StudentFinishedLesson(student, count)
            }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/grades/{subjectId}")
    fun getGradesForSubject(
        @PathVariable subjectId: String
    ): ResponseEntity<List<StudentGrade>> {
        val response = gradeService.getGradesBySubjectId(subjectId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/assessments/{subjectId}/{userId}")
    fun getStudentAssessmentsForSubject(
        @PathVariable subjectId: String,
        @PathVariable userId: String
    ): ResponseEntity<List<Assessment>> {
        val response = assessmentService.getStudentAssessmentsForSubject(subjectId, userId)
        return ResponseEntity.ok(response)
    }

    @RequestMapping(method = [RequestMethod.GET], value = ["/assessment/{lessonId}/{studentId}"])
    fun isStudentFinishedLesson(
        @PathVariable lessonId: String,
        @PathVariable studentId: String
    ): ResponseEntity<Boolean> {
        val result = assessmentService.getAssessmentForStudentByLessonId(lessonId, studentId)
        return ResponseEntity.ok(result != null)
    }
}
