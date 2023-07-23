package com.zlrx.thesis.notificationservice

import com.zlrx.thesis.notificationservice.domain.NotificationEntity
import com.zlrx.thesis.notificationservice.domain.Status

fun NotificationEntity.Companion.withTestData() = NotificationEntity(
    id = "1",
    userId = "4",
    title = "Test title",
    text = "Test text",
    status = Status.NEW,
)
