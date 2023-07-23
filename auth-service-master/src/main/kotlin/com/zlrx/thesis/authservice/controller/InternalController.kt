package com.zlrx.thesis.authservice.controller

import com.zlrx.thesis.authservice.controller.model.UserResponse
import com.zlrx.thesis.authservice.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("internal/api/v1")
class InternalController(
    private val service: UserService
) {

    @GetMapping("/users")
    fun getUsers(
        @RequestParam userIds: List<String>
    ) = service.findAllByIdIn(userIds).map { UserResponse.fromUser(it) }
}
