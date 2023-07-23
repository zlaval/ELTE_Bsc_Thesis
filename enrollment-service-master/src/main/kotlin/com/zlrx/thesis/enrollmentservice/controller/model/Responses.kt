package com.zlrx.thesis.enrollmentservice.controller.model

import com.zlrx.thesis.enrollmentservice.api.auth.User

data class CountResponse(
    val count: Int
)

data class SubjectEnrollers(
    val subjectId: String,
    val students: List<User>
)
