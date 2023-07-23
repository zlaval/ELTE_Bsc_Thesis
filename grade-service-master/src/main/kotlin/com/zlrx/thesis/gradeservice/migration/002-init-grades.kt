package com.zlrx.thesis.gradeservice.migration

import com.zlrx.thesis.gradeservice.config.withAuthContext
import com.zlrx.thesis.gradeservice.domain.Grade
import io.mongock.api.annotations.ChangeUnit

import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.springframework.data.mongodb.core.MongoTemplate

@ChangeUnit(id = "002-init-grades", order = "2", author = "zlaval")
class `002-init-grades` {
    @Execution
    fun execution(mongoTemplate: MongoTemplate) {
        val grade1 = Grade(
            id = "1",
            subjectId = "3",
            userId = "4",
            mark = 4,
            points = 27,
            credit = 3
        )

        withAuthContext("1") {
            mongoTemplate.insert(grade1)
        }
    }

    @RollbackExecution
    fun rollbackExecution(mongoTemplate: MongoTemplate) {
    }
}
