package com.zlrx.thesis.notificationservice.repository

import com.zlrx.thesis.notificationservice.domain.NotificationEntity
import com.zlrx.thesis.notificationservice.domain.Status
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.repository.MongoRepository

interface NotificationRepository : MongoRepository<NotificationEntity, String> {

    fun findByUserIdOrderByCreatedAtDesc(userId: String, page: PageRequest): Page<NotificationEntity>
    fun countByUserIdAndStatus(userId: String, status: Status): Long
}
