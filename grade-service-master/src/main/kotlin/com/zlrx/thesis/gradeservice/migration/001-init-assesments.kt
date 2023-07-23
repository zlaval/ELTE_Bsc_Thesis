package com.zlrx.thesis.gradeservice.migration

import com.zlrx.thesis.gradeservice.config.withAuthContext
import com.zlrx.thesis.gradeservice.domain.Assessment
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.springframework.data.mongodb.core.MongoTemplate

@ChangeUnit(id = "001-init-assesments", order = "1", author = "zlaval")
class `001-init-assesments` {

    @Execution
    fun execution(mongoTemplate: MongoTemplate) {
        val assessment = Assessment(
            id = "1",
            subjectId = "3",
            lessonId = "4",
            studentId = "4",
            points = 4,
            maxPoints = 4,
            solutionId = "1"
        )

        withAuthContext("1") {
            mongoTemplate.insert(assessment)
        }
    }

    @RollbackExecution
    fun rollbackExecution(mongoTemplate: MongoTemplate) {
    }
}
