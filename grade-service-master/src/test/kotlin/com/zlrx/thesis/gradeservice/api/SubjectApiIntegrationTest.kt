package com.zlrx.thesis.gradeservice.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.zlrx.thesis.gradeservice.WithTestContainers
import com.zlrx.thesis.gradeservice.api.subject.Subject
import com.zlrx.thesis.gradeservice.api.subject.SubjectApi
import com.zlrx.thesis.gradeservice.withTestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock

@SpringBootTest(
    properties = [
        "knowhere.service.subject=http://localhost:\${wiremock.server.port}"
    ]
)
@WithTestContainers
@AutoConfigureWireMock(port = 0)
class SubjectApiIntegrationTest {

    @Autowired
    lateinit var subjectApi: SubjectApi

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `subject api should provide subjects`() {
        val expectedSubjects = listOf(Subject.withTestData())
        stubFor(
            get(urlEqualTo("/internal/api/v1?subjectIds=1"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(expectedSubjects))
                )
        )

        val subjects = subjectApi.getSubjects(listOf("1"))

        assertThat(subjects).isNotEmpty
        assertThat(subjects).isEqualTo(expectedSubjects)
    }
}
