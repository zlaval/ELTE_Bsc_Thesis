package com.zlrx.thesis.gradeservice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.zlrx.thesis.gradeservice.WithStudent
import com.zlrx.thesis.gradeservice.WithTeacher
import com.zlrx.thesis.gradeservice.WithTestContainers
import com.zlrx.thesis.gradeservice.controller.model.GradeResponse
import com.zlrx.thesis.gradeservice.service.GradeService
import com.zlrx.thesis.gradeservice.withTestData
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
class GradeControllerTest {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var gradeService: GradeService

    @Test
    @WithStudent
    fun `should return with the stundet grades`() {
        whenever(gradeService.getMyGrades()) doReturn listOf(GradeResponse.withTestData())

        mockMvc.get("/api/v1")
            .andExpect {
                status { isOk() }
                content { objectMapper.writeValueAsString(listOf(GradeResponse.withTestData())) }
            }
    }

    @Test
    @WithTeacher
    fun `should be forbidden if user is not student`() {
        mockMvc.get("/api/v1")
            .andExpect {
                status { isForbidden() }
            }
    }
}
