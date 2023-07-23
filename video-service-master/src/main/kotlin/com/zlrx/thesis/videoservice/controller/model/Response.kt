package com.zlrx.thesis.videoservice.controller.model

import com.zlrx.thesis.videoservice.domain.VideoMetadata

data class VideoSelect(
    val id: String,
    val name: String,
    val thumbnail: String? = null
) {
    companion object
}

fun VideoSelect.Companion.fromMeta(video: VideoMetadata) = VideoSelect(
    id = video.id,
    name = video.title,
    thumbnail = video.thumbnail
)
