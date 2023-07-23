package com.zlrx.thesis.subjectservice.service

import com.zlrx.thesis.subjectservice.controller.model.LessonRequest
import com.zlrx.thesis.subjectservice.domain.Lesson
import com.zlrx.thesis.subjectservice.domain.Subject
import com.zlrx.thesis.subjectservice.repository.LessonRepository
import com.zlrx.thesis.subjectservice.repository.SubjectRepository
import com.zlrx.thesis.subjectservice.repository.findByIdOrThrow
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TeacherLessonService(
    private val repository: LessonRepository,
    private val subjectRepository: SubjectRepository,
) : BaseService() {

    fun findLessonByIdForTeacher(id: String) = withUserFilter { repository.findByIdOrThrow(id) }

    fun findLesson(id: String) = repository.findByIdOrNull(id)

    fun lessonCountForSubject(subject: Subject) = repository.countAllBySubject(subject)

    fun saveLesson(
        subjectId: String,
        lesson: LessonRequest
    ): Lesson {
        return setAndSave(lesson, Lesson(), subjectId)
    }

    fun updateLesson(
        id: String,
        subjectId: String,
        lesson: LessonRequest
    ): Lesson {
        val lessonEntity = withUserFilter {
            repository.findByIdOrThrow(id)
        }
        return setAndSave(lesson, lessonEntity, subjectId)
    }

    fun archiveLesson(id: String) {
        val lessonEntity = withUserFilter {
            repository.findByIdOrThrow(id)
        }
        lessonEntity.archive = true
        repository.save(lessonEntity)
    }

    private fun setAndSave(
        lesson: LessonRequest,
        lessonEntity: Lesson,
        subjectId: String
    ): Lesson {
        val subject = getSubject(subjectId)
        val nextOrder = (subject.lessons.maxByOrNull { it.order ?: 1 }?.order ?: 1) + 1
        lessonEntity.also {
            it.name = lesson.name
            it.description = lesson.description
            it.videoId = lesson.videoId
            it.quizId = lesson.quizId
            it.deadLine = lesson.deadLine
            it.subject = subject
            it.order = it.order ?: nextOrder
        }
        return repository.save(lessonEntity)
    }

    private fun getSubject(subjectId: String) = withUserFilter {
        subjectRepository.findByIdOrThrow(subjectId)
    }
}
