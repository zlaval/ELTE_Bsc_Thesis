package com.zlrx.thesis.quizservice.controller

import com.zlrx.thesis.quizservice.domain.Solution
import com.zlrx.thesis.quizservice.service.SolutionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("internal/api/v1")
class InternalController(
    private val solutionService: SolutionService
) {

    @GetMapping("/solution/{quizId}/{lessonId}")
    fun isQuizSolvedByStudent(
        @PathVariable quizId: String,
        @PathVariable lessonId: String
    ): Boolean {
        val result = solutionService.getStudentSolution(quizId, lessonId)
        return result != null
    }

    @GetMapping("/solution")
    fun getSolvedQuizzesForStudent(
        @RequestParam lessonIds: List<String>,
    ): List<Solution> {
        return solutionService.getStudentSolutions(lessonIds)
    }
}
