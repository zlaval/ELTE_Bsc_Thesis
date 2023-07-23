package com.zlrx.thesis.quizservice.controller

import com.zlrx.thesis.quizservice.controller.model.StudentSolutions
import com.zlrx.thesis.quizservice.service.SolutionService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/solution")
class SolutionController(
    private val service: SolutionService
) {

    @PostMapping()
    @PreAuthorize("hasRole('STUDENT')")
    fun solve(
        @RequestBody @Valid
        solutions: StudentSolutions
    ): ResponseEntity<Void> {
        service.solveQuiz(solutions)
        return ResponseEntity.ok().build()
    }
}
