package com.zlrx.thesis.quizservice.migration

import com.zlrx.thesis.quizservice.config.withAuthContext
import com.zlrx.thesis.quizservice.domain.Answer
import com.zlrx.thesis.quizservice.domain.Question
import com.zlrx.thesis.quizservice.domain.Quiz
import com.zlrx.thesis.quizservice.domain.newId
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.springframework.data.mongodb.core.MongoTemplate

@ChangeUnit(id = "001-init-subjects", order = "1", author = "zlaval")
class `001-init-subjects` {

    @Execution
    fun execution(mongoTemplate: MongoTemplate) {
        val quiz = Quiz(
            id = "1",
            name = "Teszt kérdés sor",
            description = "Automatikusan generált kérdéssor",
            archive = false,
            questions = listOf(
                Question(
                    id = newId(),
                    question = "Milyen keretrendszert használ a program",
                    points = 4,
                    answers = listOf(
                        Answer(
                            id = "1",
                            text = "Summer",
                            accepted = false
                        ),
                        Answer(
                            id = "2",
                            text = "Spring",
                            accepted = true
                        ),
                        Answer(
                            id = "3",
                            text = "Autumn",
                            accepted = false
                        ),
                        Answer(
                            id = "4",
                            text = "Winter",
                            accepted = false
                        ),
                    )
                )
            )
        )

        withAuthContext("1", "1") {
            mongoTemplate.insert(quiz)
        }
    }

    @RollbackExecution
    fun rollbackExecution(mongoTemplate: MongoTemplate) {
    }
}
