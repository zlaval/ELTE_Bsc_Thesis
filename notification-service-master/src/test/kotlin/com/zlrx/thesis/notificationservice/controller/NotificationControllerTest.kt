package com.zlrx.thesis.notificationservice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.zlrx.thesis.notificationservice.WithStudent
import com.zlrx.thesis.notificationservice.WithTeacher
import com.zlrx.thesis.notificationservice.WithTestContainers
import com.zlrx.thesis.notificationservice.domain.NotificationEntity
import com.zlrx.thesis.notificationservice.domain.Status
import com.zlrx.thesis.notificationservice.mockPage
import com.zlrx.thesis.notificationservice.repository.NotificationRepository
import com.zlrx.thesis.notificationservice.withTestData
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.Optional

@SpringBootTest
@WithTestContainers
@AutoConfigureMockMvc
class NotificationControllerTest {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var repository: NotificationRepository

    @Test
    @WithTeacher
    fun `it should return 403 forbidden when user has not student role`() {
        mockMvc.perform(get("/api/v1").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden)
    }

    @Test
    @WithStudent
    fun `should return user's notifications`() {
        whenever(
            repository.findByUserIdOrderByCreatedAtDesc(
                any(),
                any()
            )
        ) doReturn mockPage(NotificationEntity.withTestData())

        mockMvc.get("/api/v1?page=0")
            .andExpect {
                status { isOk() }
                content { objectMapper.writeValueAsString(NotificationEntity.withTestData()) }
            }
    }

    @Test
    @WithStudent
    fun `should return number of notifications`() {
        whenever(repository.countByUserIdAndStatus(any(), any())) doReturn 5

        mockMvc.get("/api/v1/count")
            .andExpect {
                status { isOk() }
                content { jsonPath("$").value(5) }
            }
    }

    @Test
    @WithStudent
    fun `should modify status and return with update notification using valid id`() {
        val expected = NotificationEntity.withTestData().copy(status = Status.READ)

        whenever(repository.findById(any())) doReturn Optional.of(NotificationEntity.withTestData())

        whenever(repository.save(expected)).thenAnswer {
            it.arguments.first()
        }

        mockMvc.patch("/api/v1/1")
            .andExpect {
                status { isOk() }
                content { objectMapper.writeValueAsString(expected) }
            }

        verify(repository).findById("1")
        verify(repository).save(expected)
        verifyNoMoreInteractions(repository)
    }

    @Test
    @WithStudent
    fun `test read with invalid id`() {

        whenever(repository.findById(any())) doReturn Optional.empty()

        mockMvc.patch("/api/v1/2")
            .andExpect {
                status { isNotFound() }
            }
        verify(repository).findById("2")
        verifyNoMoreInteractions(repository)
    }
}
