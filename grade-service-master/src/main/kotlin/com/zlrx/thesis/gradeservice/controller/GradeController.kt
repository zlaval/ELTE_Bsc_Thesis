package com.zlrx.thesis.gradeservice.controller

import com.zlrx.thesis.gradeservice.controller.model.GradeResponse
import com.zlrx.thesis.gradeservice.service.GradeService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class GradeController(
    private val service: GradeService
) {

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    fun getMyGrades(): ResponseEntity<List<GradeResponse>> {
        return ResponseEntity.ok(service.getMyGrades())
    }
}
