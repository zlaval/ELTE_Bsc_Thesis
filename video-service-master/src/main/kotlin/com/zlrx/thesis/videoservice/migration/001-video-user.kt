package com.zlrx.thesis.videoservice.migration

import com.zlrx.thesis.videoservice.config.withAuthContext
import com.zlrx.thesis.videoservice.domain.VideoMetadata
import com.zlrx.thesis.videoservice.service.THUMBNAIL_DIR
import com.zlrx.thesis.videoservice.service.VIDEO_DIR
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.ResourceLoader
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.util.FileCopyUtils
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

@ChangeUnit(id = "001-video-user", order = "1", author = "zlaval")
class `001-video-user`(
    private val resourceLoader: ResourceLoader,
) {

    @Execution
    fun execution(mongoTemplate: MongoTemplate) {
        try {
            val normalVideo = createVideo("1")
            val archivedVideo = createVideo("2").copy(
                archived = true
            )
            val publicVideo = createVideo("3").copy(
                public = true
            )

            withAuthContext("1", "1") {
                mongoTemplate.insert(normalVideo)
                mongoTemplate.insert(archivedVideo)
            }

            withAuthContext("1", "2") {
                mongoTemplate.insert(publicVideo)
            }
        } catch (e: Exception) {
            // no test data in this case
        }
    }

    private fun createVideo(id: String): VideoMetadata {
        val originalFileName = "test_video.mp4"
        checkAndCreateDir()

        val video = ClassPathResource("video/$originalFileName")
        val thumbnail = ClassPathResource("video/thumbnail.jpg")

        val fileName = "${id}_$originalFileName"
        val thumbnailName = "${id}_thumbnail.jpg"

        val videoDestination = File("$VIDEO_DIR/$fileName")
        val thumbnailDestination = File("$THUMBNAIL_DIR/$thumbnailName")

        FileCopyUtils.copy(video.file, videoDestination)
        FileCopyUtils.copy(thumbnail.file, thumbnailDestination)

        return VideoMetadata(
            id = id,
            title = "Test video ($id)",
            fileName = fileName,
            originalFileName = originalFileName,
            public = false,
            archived = false,
            thumbnail = thumbnailName,
        )
    }

    private fun checkAndCreateDir() {
        val videoDir = Path.of(VIDEO_DIR)
        Files.createDirectories(videoDir)
        val thumbnailDir = Path.of(THUMBNAIL_DIR)
        Files.createDirectories(thumbnailDir)
    }

    @RollbackExecution
    fun rollbackExecution(mongoTemplate: MongoTemplate) {
    }
}
