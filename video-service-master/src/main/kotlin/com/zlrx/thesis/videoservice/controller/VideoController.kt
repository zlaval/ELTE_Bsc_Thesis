package com.zlrx.thesis.videoservice.controller

import com.zlrx.thesis.videoservice.controller.model.VideoSelect
import com.zlrx.thesis.videoservice.controller.model.fromMeta
import com.zlrx.thesis.videoservice.domain.VideoMetadata
import com.zlrx.thesis.videoservice.service.VideoService
import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController

import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1")
class VideoController(
    private val service: VideoService
) {

    private val logger = KotlinLogging.logger {}

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    fun upload(
        @RequestPart("file", required = true) file: MultipartFile,
        @RequestPart("title", required = true) title: String,
        @RequestPart("public") public: String?
    ): ResponseEntity<VideoMetadata> {
        val response = service.saveVideo(file, title, public.toBoolean())
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/video-select")
    @PreAuthorize("hasRole('TEACHER')")
    fun getVideoMetadataForSelect(): ResponseEntity<List<VideoSelect>> {
        val selectData = service.getAllAvailableVideos().map {
            VideoSelect.fromMeta(it)
        }
        return ResponseEntity.ok(selectData)
    }

    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    fun getMyVideos(
        @RequestParam(defaultValue = "false") archive: Boolean,
        @RequestParam(name = "page", defaultValue = "0")
        pageNumber: Int
    ): ResponseEntity<Page<VideoMetadata>> {
        val page = PageRequest.of(pageNumber, 5)
        val response = service.getMyVideos(archive, page)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/public")
    @PreAuthorize("hasRole('TEACHER')")
    fun getPublicVideos(
        @RequestParam(name = "page", defaultValue = "0")
        pageNumber: Int
    ): ResponseEntity<Page<VideoMetadata>> {
        val page = PageRequest.of(pageNumber, 8)
        val response = service.getPublicVideos(page)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','STUDENT')")
    fun getVideo(@PathVariable id: String): ResponseEntity<VideoMetadata> {
        val result = service.getVideo(id)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/archive/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    fun archive(
        @PathVariable id: String
    ): ResponseEntity<Void> {
        service.archive(id)
        return ResponseEntity.noContent().build()
    }
}
