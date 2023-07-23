package com.zlrx.thesis.videoservice.controller

import com.zlrx.thesis.videoservice.service.VideoService
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/resource")
class ResourceController(
    private val service: VideoService
) {

    @GetMapping(
        path = ["/image/{name}"],
        produces = [MediaType.IMAGE_JPEG_VALUE]
    )
    fun getImage(
        @PathVariable("name") name: String
    ): Resource {
        return service.getImage(name)
    }

    @GetMapping(
        path = ["/play/{id}/{name}"],
        produces = ["video/mp4"]
    )
    fun playVideo(
        @PathVariable("id") id: String,
        @PathVariable("name") name: String,
        @RequestHeader("Range", required = false) range: String?
    ): Resource {
        return service.playVideo(id)
    }
}
