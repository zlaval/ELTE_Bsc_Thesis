package com.zlrx.thesis.subjectservice.repository

import com.zlrx.thesis.subjectservice.config.ApiException
import com.zlrx.thesis.subjectservice.config.Messages.LESSON_NOT_FOUND
import com.zlrx.thesis.subjectservice.domain.Lesson
import com.zlrx.thesis.subjectservice.domain.Subject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Transactional(propagation = Propagation.MANDATORY)
interface LessonRepository : JpaRepository<Lesson, String> {

    fun countAllBySubject(subject: Subject): Long
}

fun LessonRepository.findByIdOrThrow(id: String): Lesson =
    this.findById(id).orElseThrow {
        ApiException(LESSON_NOT_FOUND, HttpStatus.NOT_FOUND, id)
    }
