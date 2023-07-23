package com.zlrx.thesis.gradeservice.service

import com.zlrx.thesis.gradeservice.api.auth.AuthIntegration
import com.zlrx.thesis.gradeservice.api.auth.User
import com.zlrx.thesis.gradeservice.api.subject.Subject
import com.zlrx.thesis.gradeservice.api.subject.SubjectIntegration
import com.zlrx.thesis.gradeservice.config.MessageResolver
import com.zlrx.thesis.gradeservice.config.Messages
import com.zlrx.thesis.gradeservice.controller.model.GradeResponse
import com.zlrx.thesis.gradeservice.domain.Grade
import com.zlrx.thesis.gradeservice.repository.GradeRepository
import com.zlrx.thesis.gradeservice.withTestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.amqp.rabbit.core.RabbitTemplate

class GradeServiceTest {

    private val repository: GradeRepository = mock() {
        on { findAllByUserId("1") } doReturn listOf(Grade.withTestData())
        on { findAllByUserId("2") } doReturn emptyList()
        on { findAllBySubjectId("1", "1") } doReturn listOf(Grade.withTestData())
        on { save(any<Grade>()) } doReturn Grade.withTestData()
    }
    private val authIntegration: AuthIntegration = mock() {
        on { getUsers(any()) } doReturn listOf(User.withTestData())
    }
    private val subjectIntegration: SubjectIntegration = mock() {
        on { getSubjects(listOf("1")) } doReturn listOf(Subject.withTestData())
    }
    private val rabbitTemplate: RabbitTemplate = mock()
    private val userProvider: InjectableUserProvider = mock() {
        on { getTenantId() } doReturn "1"
    }

    private val messageResolver: MessageResolver = mock() {
        on { getMessage(any<String>(), anyOrNull()) } doReturn "Mock"
        on { getMessage(any<Messages>(), anyOrNull()) } doReturn "Mock"
    }

    private val sut = GradeService(
        repository,
        authIntegration,
        subjectIntegration,
        rabbitTemplate,
        messageResolver,
        userProvider
    )

    private fun verifyAllNoMoreInteraction() {
        verifyNoMoreInteractions(repository)
        verifyNoMoreInteractions(authIntegration)
        verifyNoMoreInteractions(subjectIntegration)
        verifyNoMoreInteractions(rabbitTemplate)
    }

    @Test
    fun `should return empty list if no grade`() {
        whenever(userProvider.getUserId()) doReturn "2"

        val response = sut.getMyGrades()

        assertThat(response).isEmpty()
        verify(repository).findAllByUserId("2")
        verifyAllNoMoreInteraction()
    }

    @Test
    fun `should return with the grades`() {
        whenever(userProvider.getUserId()) doReturn "1"

        val response = sut.getMyGrades()

        assertThat(response).hasSize(1)
        assertThat(response.first()).isEqualTo(GradeResponse.withTestData())

        verify(repository).findAllByUserId("1")
        verify(subjectIntegration).getSubjects(listOf("1"))
        verifyAllNoMoreInteraction()
    }

    @Test
    fun `should return grades for the subject`() {

        val result = sut.getGradesBySubjectId("1")
        assertThat(result).hasSize(1)
        val user = result.first()
        assertThat(user.mark).isEqualTo(5)
        assertThat(user.points).isEqualTo(25)
        assertThat(user.studentName).isEqualTo("Joe Doe")

        verify(repository).findAllBySubjectId("1", "1")
        verify(authIntegration).getUsers(listOf("1"))
        verifyAllNoMoreInteraction()
    }

    @Test
    fun `should save mark and send rabbit message`() {

        sut.gradeStudent("1", "2", 4, 25, 50, "Programming")

        argumentCaptor<Grade> {
            verify(repository).save(capture())

            assertThat(firstValue.subjectId).isEqualTo("2")
            assertThat(firstValue.userId).isEqualTo("1")
            assertThat(firstValue.mark).isEqualTo(2)
        }

        verify(repository).save(any())
        verify(rabbitTemplate).convertAndSend(any(), any<Grade>())
        verify(rabbitTemplate).convertAndSend(any(), any<NotificationMessage>())
        verifyAllNoMoreInteraction()
    }
}
