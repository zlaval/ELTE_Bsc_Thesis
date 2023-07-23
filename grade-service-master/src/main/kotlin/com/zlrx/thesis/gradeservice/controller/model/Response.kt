package com.zlrx.thesis.gradeservice.controller.model

data class StudentFinishedLesson(
    val studentId: String,
    val count: Int
) {
    companion object
}

data class StudentGrade(
    val studentName: String,
    val email: String,
    val mark: Int,
    val points: Int
) {
    companion object
}

data class GradeResponse(
    val id: String,
    val subject: String,
    val credit: Int,
    val points: Int,
    val mark: Int
) {
    companion object
}
