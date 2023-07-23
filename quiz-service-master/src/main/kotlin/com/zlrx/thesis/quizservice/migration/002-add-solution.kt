package com.zlrx.thesis.quizservice.migration

import com.zlrx.thesis.quizservice.config.withAuthContext
import com.zlrx.thesis.quizservice.domain.ProcessStatus
import com.zlrx.thesis.quizservice.domain.QuestionAnswer
import com.zlrx.thesis.quizservice.domain.Solution
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.springframework.data.mongodb.core.MongoTemplate
import java.time.Instant

@ChangeUnit(id = "002-add-solution", order = "2", author = "zlaval")
class `002-add-solution` {

    @Execution
    fun execution(mongoTemplate: MongoTemplate) {
        val solution = Solution(
            id = "1",
            quizId = "1",
            lessonId = "4",
            studentId = "4",
            answers = listOf(QuestionAnswer("1", "2")),
            status = ProcessStatus.FINISHED,
            attempts = 1,
            lastAttempt = Instant.now()
        )
        withAuthContext("1", "1") {
            mongoTemplate.insert(solution)
        }
    }

    @RollbackExecution
    fun rollbackExecution(mongoTemplate: MongoTemplate) {
    }
}