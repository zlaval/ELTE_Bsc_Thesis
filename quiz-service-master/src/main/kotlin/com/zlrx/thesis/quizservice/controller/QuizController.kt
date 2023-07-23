package com.zlrx.thesis.quizservice.controller

import com.zlrx.thesis.quizservice.controller.model.EditQuizRequest
import com.zlrx.thesis.quizservice.controller.model.QuizResponse
import com.zlrx.thesis.quizservice.controller.model.fromQuiz
import com.zlrx.thesis.quizservice.service.QuizService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1")
class QuizController(
    private val service: QuizService
) {

    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    fun myQuizzes(): ResponseEntity<List<QuizResponse>> {
        val quizzes = service.loadMyQuizzes().map {
            QuizResponse.fromQuiz(it)
        }
        return ResponseEntity.ok().body(quizzes)
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    fun archive(@PathVariable id: String): ResponseEntity<Void> {
        service.archive(id)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','STUDENT')")
    fun quiz(@PathVariable id: String): ResponseEntity<QuizResponse> = ResponseEntity.ok()
        .body(QuizResponse.fromQuiz(service.findById(id)))

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    fun update(
        @PathVariable id: String,
        @RequestBody @Valid
        request: EditQuizRequest
    ): ResponseEntity<QuizResponse> {
        val quiz = service.update(id, request)
        return ResponseEntity.ok().body(QuizResponse.fromQuiz(quiz))
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    fun save(
        @RequestBody @Valid
        request: EditQuizRequest
    ): ResponseEntity<QuizResponse> {
        val quiz = service.save(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(QuizResponse.fromQuiz(quiz))
    }
}
