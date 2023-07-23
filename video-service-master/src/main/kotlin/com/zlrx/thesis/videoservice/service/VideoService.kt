package com.zlrx.thesis.videoservice.service

import com.zlrx.thesis.videoservice.config.ApiException
import com.zlrx.thesis.videoservice.config.Messages.VIDEO_NOT_FOUND
import com.zlrx.thesis.videoservice.config.SYSTEM
import com.zlrx.thesis.videoservice.config.withAuthContext
import com.zlrx.thesis.videoservice.domain.VideoMetadata
import com.zlrx.thesis.videoservice.domain.newId
import com.zlrx.thesis.videoservice.repository.VideoMetadataRepository
import com.zlrx.thesis.videoservice.repository.findByIdOrThrow
import mu.KotlinLogging
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.Java2DFrameConverter
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO

@Component
class VideoCreateEventListener(
    private val repository: VideoMetadataRepository,
) {

    private val logger = KotlinLogging.logger {}

    @Async
    @EventListener
    fun handleEvent(video: VideoMetadata) {
        withAuthContext(video.tenantId, video.createdBy ?: SYSTEM) {
            logger.info { "Processing video ${video.id}" }
            createThumbnail(video)
            logger.info { "Processing video ${video.id} has been finished" }
        }
    }

    private fun createThumbnail(video: VideoMetadata) {
        try {
            val grabber = FFmpegFrameGrabber("$VIDEO_DIR/${video.fileName}")
            try {
                grabber.start()
                val thumbnailName = "${newId()}.jpeg"
                val converter = Java2DFrameConverter()

                var frame = grabber.grabKeyFrame()
                if (frame == null) {
                    for (i in 0..99)
                        grabber.grabImage()
                    // Fallback to 100. frame
                    frame = grabber.grabImage()
                }
                val bufferedImage = converter.convert(frame)
                ImageIO.write(bufferedImage, "jpeg", File("$THUMBNAIL_DIR/$thumbnailName"))
                val updated = video.copy(thumbnail = thumbnailName)
                repository.save(updated)
            } finally {
                grabber.stop()
            }
        } catch (e: Throwable) {
            logger.error { "Cannot create thumbnail for ${video.id}" }
        }
    }
}

const val VIDEO_DIR = "/data/video"
const val THUMBNAIL_DIR = "/data/thumbnail"

@Service
class VideoService(
    private val resourceLoader: ResourceLoader,
    private val repository: VideoMetadataRepository,
    private val publisher: ApplicationEventPublisher
) : BaseService() {

    fun saveVideo(file: MultipartFile, title: String, public: Boolean): VideoMetadata {
        val id = newId()
        val requestFilename = file.originalFilename
        val originalFileName =
            (requestFilename?.slice(0..requestFilename.lastIndexOf(".")) ?: "noname")
        val fileName = "${id}_$originalFileName.mp4"

        writeFile(file, fileName)

        val videoMetadata = VideoMetadata(
            id = id,
            title = title,
            fileName = fileName,
            originalFileName = originalFileName,
            public = public,
        )

        val result = repository.save(videoMetadata)
        publisher.publishEvent(result)
        return result
    }

    fun getAllAvailableVideos(): List<VideoMetadata> {
        return repository.getAllAvailableVideos(getUserId())
    }

    private fun writeFile(file: MultipartFile, fileName: String) {
        checkAndCreateDir()
        file.transferTo(Path.of("$VIDEO_DIR/$fileName"))
    }

    fun playVideo(id: String): Resource {
        val video = repository.findByIdOrThrow(id)
        return this.resourceLoader.getResource("file:$VIDEO_DIR/${video.fileName}")
    }

    fun getImage(name: String): Resource {
        return this.resourceLoader.getResource("file:$THUMBNAIL_DIR/$name")
    }

    fun getMyVideos(archive: Boolean, page: PageRequest): Page<VideoMetadata> {
        val userId = getUserId()
        return if (archive) {
            repository.findAllByCreatedBy(userId, page)
        } else {
            repository.findAllByCreatedByAndArchivedFalse(userId, page)
        }
    }

    fun archive(id: String) {
        val video = repository.findOneByIdAndCreatedBy(id, getUserId())
            ?: throw ApiException(VIDEO_NOT_FOUND, HttpStatus.NOT_FOUND, id)

        val updated = video.copy(
            archived = true
        )
        repository.save(updated)
    }

    fun getVideo(id: String): VideoMetadata {
        return repository.findByIdOrThrow(id)
    }

    fun getPublicVideos(page: PageRequest): Page<VideoMetadata> {
        val user = getUser()
        return repository.findAllByPublicTrueAndArchivedFalseAndCreatedByIsNotAndTenantId(user.id, user.tenantId, page)
    }

    private fun checkAndCreateDir() {
        val path = Path.of(VIDEO_DIR)
        Files.createDirectories(path)
    }
}
