package com.zlrx.thesis.notificationservice.controller

import com.zlrx.thesis.notificationservice.config.ApiException
import com.zlrx.thesis.notificationservice.config.Messages
import com.zlrx.thesis.notificationservice.config.authInfo
import com.zlrx.thesis.notificationservice.config.orBlow
import com.zlrx.thesis.notificationservice.domain.NotificationEntity
import com.zlrx.thesis.notificationservice.domain.Status
import com.zlrx.thesis.notificationservice.repository.NotificationRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1")
@PreAuthorize("hasRole('STUDENT')")
class NotificationController(
    private val repository: NotificationRepository
) {

    @GetMapping
    fun myNotifications(
        @RequestParam(name = "page", defaultValue = "0")
        pageNumber: Int
    ): Page<NotificationEntity> {
        val user = authInfo().orBlow()
        val page = PageRequest.of(pageNumber, 10)
        return repository.findByUserIdOrderByCreatedAtDesc(user.id, page)
    }

    @GetMapping("/count")
    fun countNotifications(): Long {
        val user = authInfo().orBlow()
        return repository.countByUserIdAndStatus(user.id, Status.NEW)
    }

    @PatchMapping("/{id}")
    fun read(
        @PathVariable id: String
    ): NotificationEntity {
        val notification = repository.findByIdOrNull(id)
        if (notification != null) {
            return repository.save(notification.copy(status = Status.READ))
        }
        throw ApiException(Messages.NOT_FOUND, HttpStatus.NOT_FOUND, id)
    }
}
