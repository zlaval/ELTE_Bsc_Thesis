package com.zlrx.thesis.gradeservice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.zlrx.thesis.gradeservice.WithTestContainers
import com.zlrx.thesis.gradeservice.controller.model.StudentFinishedLesson
import com.zlrx.thesis.gradeservice.controller.model.StudentGrade
import com.zlrx.thesis.gradeservice.domain.Assessment
import com.zlrx.thesis.gradeservice.service.AssessmentService
import com.zlrx.thesis.gradeservice.service.GradeService
import com.zlrx.thesis.gradeservice.withTestData
import org.hamcrest.core.IsEqual
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@WithTestContainers
@AutoConfigureMockMvc
class InternalControllerTest {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var assessmentService: AssessmentService

    @MockBean
    private lateinit var gradeService: GradeService

    @Test
    fun `should return with students' progress`() {
        whenever(assessmentService.getStudentsProgressForSubject("1")) doReturn mapOf("1" to 3)
        mockMvc.get("/internal/api/v1/progress/1").andExpect {
            status { isOk() }
            content { objectMapper.writeValueAsString(listOf(StudentFinishedLesson("1", 3))) }
        }
    }

    @Test
    fun `should return with grades for subject`() {
        val subjectId = "test-subject-id"
        whenever(gradeService.getGradesBySubjectId(subjectId)) doReturn listOf(
            StudentGrade.withTestData()
        )

        mockMvc.get("/internal/api/v1/grades/1")
            .andExpect {
                status { isOk() }
                content {
                    objectMapper.writeValueAsString(
                        listOf(
                            StudentGrade.withTestData()
                        )
                    )
                }
            }
    }

    @Test
    fun `should return all assessments of a student for a subject`() {
        val expected = listOf(
            Assessment.withTestData(studentId = "2"),
            Assessment.withTestData(studentId = "2", lessonId = "2"),
        )

        whenever(assessmentService.getStudentAssessmentsForSubject("1", "2")) doReturn (
                expected
                )

        mockMvc.get("/internal/api/v1/assessments/1/2")
            .andExpect {
                status { isOk() }
                content { objectMapper.writeValueAsString(expected) }
            }
    }

    @Test
    fun `should return true if user finished the lesson`() {
        whenever(assessmentService.getAssessmentForStudentByLessonId("1", "2")) doReturn Assessment.withTestData()

        mockMvc.get("/internal/api/v1/assessment/1/2")
            .andExpect {
                status { isOk() }
                jsonPath("$", IsEqual(true))
            }
    }

    @Test
    fun `should return false if user does not finished the lesson`() {
        whenever(assessmentService.getAssessmentForStudentByLessonId("1", "2")) doReturn null

        mockMvc.get("/internal/api/v1/assessment/1/2")
            .andExpect {
                status { isOk() }
                jsonPath("$", IsEqual(false))
            }
    }
}
