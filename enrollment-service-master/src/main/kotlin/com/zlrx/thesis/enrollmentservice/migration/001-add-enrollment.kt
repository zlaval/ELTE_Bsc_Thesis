package com.zlrx.thesis.enrollmentservice.migration

import com.zlrx.thesis.enrollmentservice.domain.Enrollment
import com.zlrx.thesis.enrollmentservice.domain.EnrollmentStatus
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.springframework.data.mongodb.core.MongoTemplate

@ChangeUnit(id = "001-add-enrollment", order = "1", author = "zlaval")
class `001-add-enrollment` {

    @Execution
    fun execution(mongoTemplate: MongoTemplate) {

        val finished = Enrollment(
            id = "1",
            subjectId = "3",
            studentId = "4",
            status = EnrollmentStatus.FINISHED,
            tenantId = "1"
        )

        mongoTemplate.insert(finished)
    }

    @RollbackExecution
    fun rollbackExecution(mongoTemplate: MongoTemplate) {
    }
}
