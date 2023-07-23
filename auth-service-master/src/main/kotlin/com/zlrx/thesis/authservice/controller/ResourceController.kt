package com.zlrx.thesis.authservice.controller

import com.zlrx.thesis.authservice.service.UserService
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/resource")
class ResourceController(
    private val service: UserService
) {

    @GetMapping("/image/{path}", produces = [MediaType.IMAGE_JPEG_VALUE])
    fun getCoverImage(
        @PathVariable path: String
    ): Resource {
        return service.loadFile(path)
    }
}
