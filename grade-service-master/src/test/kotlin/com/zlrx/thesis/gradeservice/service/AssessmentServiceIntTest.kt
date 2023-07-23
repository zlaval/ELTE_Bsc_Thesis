package com.zlrx.thesis.gradeservice.service

import com.zlrx.thesis.gradeservice.WithStudent
import com.zlrx.thesis.gradeservice.WithTeacher
import com.zlrx.thesis.gradeservice.WithTestContainers
import com.zlrx.thesis.gradeservice.domain.Assessment
import com.zlrx.thesis.gradeservice.domain.newId
import com.zlrx.thesis.gradeservice.repository.AssessmentRepository
import com.zlrx.thesis.gradeservice.withTestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@WithTestContainers
class AssessmentServiceIntTest {

    @Autowired
    private lateinit var repository: AssessmentRepository

    @Autowired
    private lateinit var sut: AssessmentService

    @BeforeEach
    fun init() {
        repository.deleteAll()

        repository.save(Assessment.withTestData("1", "1", "1", "1"))
        repository.save(Assessment.withTestData("2", "1", "2", "1"))
        repository.save(Assessment.withTestData("3", "1", "2", "2"))
        repository.save(Assessment.withTestData("4", "1", "3", "2"))
        repository.save(Assessment.withTestData("5", "1", "3", "1"))
        repository.save(Assessment.withTestData("6", "2", "4", "1"))
        repository.save(Assessment.withTestData("7", "2", "5", "1"))
        repository.save(Assessment.withTestData("8", "2", "5", "2"))
        repository.save(Assessment.withTestData("9", "2", "6", "2"))
    }

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }

    @Test
    @WithTeacher
    fun `should return progresses for subject`() {
        val result = sut.getStudentsProgressForSubject("1")
        assertThat(result).hasSize(2)
        assertThat(result["1"]).isEqualTo(3)
        assertThat(result["2"]).isEqualTo(2)
    }

    @Test
    @WithStudent
    fun `should return assessments of the student for subject`() {
        val expectedLessonIds = listOf("2", "3")
        val result = sut.getStudentAssessmentsForSubject("1", "2")

        assertThat(result).hasSize(2)
        assertThat(result).allMatch { expectedLessonIds.contains(it.lessonId) }
    }

    @Test
    @WithStudent
    fun `should return with the given assessment when student has finished the lesson`() {
        val result = sut.getAssessmentForStudentByLessonId("5", "1")
        assertThat(result).isNotNull
        assertThat(result?.lessonId).isEqualTo("5")
    }

    @Test
    @WithStudent
    fun `should return null when user not finished the lesson`() {
        val result = sut.getAssessmentForStudentByLessonId("6", "1")
        assertThat(result).isNull()
    }

    @Test
    @WithTeacher
    fun `should save the assessment`() {
        val assessment = Assessment.withTestData(
            id = newId(),
            subjectId = "3",
            studentId = "7",
            lessonId = "10"
        )

        val result = sut.saveAssessment(assessment)

        assertThat(repository.findById(result.id).orElse(null)).isNotNull
    }
}
