package com.zlrx.thesis.quizservice.controller

import com.zlrx.thesis.quizservice.WithTestContainers
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest
@WithTestContainers
@AutoConfigureMockMvc
class QuizControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun test() {
    }
}
