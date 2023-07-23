package com.zlrx.thesis.subjectservice.controller.model

import java.time.Instant

data class StudentProgress(
    val id: String,
    val name: String,
    val finishedLessons: Int
)

data class Grade(
    val studentName: String,
    val email: String,
    val mark: Int,
    val points: Int
)

data class SubjectWithGrades(
    val name: String,
    val grades: List<Grade>
)

data class SubjectForSelect(
    val id: String,
    val name: String
)

data class ActiveSubjectLessons(
    val name: String,
    val description: String?,
    var lessons: List<ActiveLesson>
)

data class ActiveLesson(
    val id: String,
    val name: String,
    val description: String?,
    val finished: Boolean,
    val deadLine: Instant?,
    val points: Int?,
    val maxPoint: Int?
)
