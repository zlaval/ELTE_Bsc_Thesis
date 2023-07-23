package com.zlrx.thesis.gradeservice

import com.zlrx.thesis.gradeservice.api.auth.User
import com.zlrx.thesis.gradeservice.api.subject.Subject
import com.zlrx.thesis.gradeservice.controller.model.GradeResponse
import com.zlrx.thesis.gradeservice.controller.model.StudentGrade
import com.zlrx.thesis.gradeservice.domain.Assessment
import com.zlrx.thesis.gradeservice.domain.Grade

const val TEST_TENANT_ID = "1"

fun User.Companion.withTestData() = User(
    id = "1",
    name = "Joe Doe",
    email = "joe@gmail.com"
)

fun GradeResponse.Companion.withTestData() = GradeResponse(
    id = "1",
    subject = "Java",
    credit = 4,
    points = 25,
    mark = 5
)

fun Grade.Companion.withTestData() = Grade(
    id = "1",
    subjectId = "1",
    userId = "1",
    mark = 5,
    points = 25,
    credit = 4
)

fun Subject.Companion.withTestData() = Subject(
    id = "1",
    name = "Java"
)

fun StudentGrade.Companion.withTestData() = StudentGrade(
    studentName = "Joe Doe",
    email = "joe@gmail.com",
    mark = 4,
    points = 10
)

fun Assessment.Companion.withTestData(
    id: String = "1",
    subjectId: String = "1",
    lessonId: String = "1",
    studentId: String = "1"
) = Assessment(
    id = id,
    subjectId = subjectId,
    lessonId = lessonId,
    studentId = studentId,
    points = 10,
    maxPoints = 15,
    solutionId = "1"
).also {
    it.tenantId = TEST_TENANT_ID
}
