package com.zlrx.thesis.gradeservice.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.zlrx.thesis.gradeservice.WithTestContainers
import com.zlrx.thesis.gradeservice.api.auth.AuthApi
import com.zlrx.thesis.gradeservice.api.auth.User
import com.zlrx.thesis.gradeservice.withTestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock

@SpringBootTest(
    properties = [
        "knowhere.service.auth=http://localhost:\${wiremock.server.port}"
    ]
)
@WithTestContainers
@AutoConfigureWireMock(port = 0)
class AuthApiIntegrationTest {

    @Autowired
    private lateinit var authApi: AuthApi

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `auth api should provide user`() {
        val expectedUsers = listOf(User.withTestData())
        stubFor(
            get(urlEqualTo("/internal/api/v1/users?userIds=1"))
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(expectedUsers))
                )
        )

        val users = authApi.getUsers(listOf("1"))

        assertThat(users).isNotEmpty
        assertThat(users).isEqualTo(expectedUsers)
    }
}
