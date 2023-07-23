package com.zlrx.thesis.gradeservice.stream

import com.zlrx.thesis.gradeservice.TEST_TENANT_ID
import com.zlrx.thesis.gradeservice.WithTestContainers
import com.zlrx.thesis.gradeservice.awaitAssert
import com.zlrx.thesis.gradeservice.config.GRADE_QUEUE_NAME
import com.zlrx.thesis.gradeservice.config.withAuthContext
import com.zlrx.thesis.gradeservice.domain.Assessment
import com.zlrx.thesis.gradeservice.repository.AssessmentRepository
import com.zlrx.thesis.gradeservice.repository.GradeRepository
import com.zlrx.thesis.gradeservice.withTestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@WithTestContainers
@Disabled
class GradeQueueListenerTest {

    @Autowired
    lateinit var rabbitTemplate: RabbitTemplate

    @Autowired
    lateinit var assessmentRepository: AssessmentRepository

    @Autowired
    lateinit var gradeRepository: GradeRepository

    @BeforeEach
    fun init() {
        withAuthContext(TEST_TENANT_ID) {
            assessmentRepository.deleteAll()
            gradeRepository.deleteAll()
            assessmentRepository.save(Assessment.withTestData("1", "1", "1", "1"))
            assessmentRepository.save(Assessment.withTestData("2", "1", "2", "1"))
        }
    }

    @Test
    fun test() {

        rabbitTemplate.convertAndSend(
            GRADE_QUEUE_NAME,
            EnhancedSolutionModel(
                subjectId = "1",
                lessonCount = 3,
                credit = 4,
                subjectName = "Test",
                solution = SolutionModel(
                    tenantId = TEST_TENANT_ID,
                    studentId = "1",
                    quizId = "1",
                    lessonId = "3",
                    points = 10,
                    maxPoints = 10,
                    solutionId = "1"
                )
            )
        )

        awaitAssert {
            val result = assessmentRepository.findAll()
            assertThat(result).hasSize(3)
            val grades = gradeRepository.findAll()
            assertThat(grades).hasSize(1)
            assertThat(grades.first().mark).isEqualTo(4)
        }
    }
}
