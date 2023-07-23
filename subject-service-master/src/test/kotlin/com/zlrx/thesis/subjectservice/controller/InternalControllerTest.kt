package com.zlrx.thesis.subjectservice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.zlrx.thesis.subjectservice.WithTeacher
import com.zlrx.thesis.subjectservice.WithTestContainers
import com.zlrx.thesis.subjectservice.domain.Subject
import com.zlrx.thesis.subjectservice.service.StudentSubjectService
import com.zlrx.thesis.subjectservice.withTestData
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
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
class InternalControllerTests {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var service: StudentSubjectService

    @Test
    @WithTeacher
    fun `should return subjects when provided with valid subjectIds`() {
        val expectedResult = listOf(
            Subject().withTestData(),
            Subject().withTestData("2")
        )

        whenever(service.findAllById(any())) doReturn expectedResult

        mockMvc.get("/internal/api/v1?subjectIds=1&subjectIds=2")
            .andExpect {
                status { isOk() }
                content {
                    objectMapper.writeValueAsString(expectedResult)
                }
            }
    }

    @Test
    fun `should return bad request when subjectIds is empty`() {
        mockMvc.get("/internal/api/v1")
            .andExpect {
                status { isBadRequest() }
            }
    }

//    @Test
//    fun `should return not found when provided with invalid subjectIds`() {
//        // given
//        val invalidSubjectId = "invalid"
//        val subjectIds = listOf(invalidSubjectId)
//        val expectedErrorMessage = "Subject $invalidSubjectId not found"
//        val expectedResponse = "{\"status\":\"NOT_FOUND\",\"error\":\"$expectedErrorMessage\"}"
//
//        // mock service
//        given(studentSubjectService.findAllById(eq(subjectIds))).willThrow(
//            NoSuchElementException(expectedErrorMessage)
//        )
//
//        // when & then
//        mockMvc.get("/internal/api/v1?subjectIds=$invalidSubjectId")
//            .andExpect {
//                status { isNotFound() }
//                content { string(expectedResponse) }
//            }
//    }
//
//    @Test
//    fun `should return internal server error when an unexpected error occurs`() {
//        // given
//        val internalServerError = "Internal Server Error"
//        val subjectIds = listOf("1")
//        val expectedResponse = "{\"status\":\"INTERNAL_SERVER_ERROR\",\"error\":\"$internalServerError\"}"
//
//        // mock service
//        given(studentSubjectService.findAllById(eq(subjectIds))).willThrow(
//            RuntimeException(internalServerError)
//        )
//
//        // when & then
//        mockMvc.get("/internal/api/v1?subjectIds=1")
//            .andExpect {
//                status { isInternalServerError() }
//                content { string(expectedResponse) }
//            }
//    }
}
