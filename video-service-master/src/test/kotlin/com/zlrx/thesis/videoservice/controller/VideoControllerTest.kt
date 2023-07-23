package com.zlrx.thesis.videoservice.controller

import com.zlrx.thesis.videoservice.WithTestContainers
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest
@WithTestContainers
@AutoConfigureMockMvc
class VideoControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun test() {
    }
}
