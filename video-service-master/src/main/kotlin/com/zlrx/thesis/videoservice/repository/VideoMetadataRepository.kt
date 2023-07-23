package com.zlrx.thesis.videoservice.repository

import com.zlrx.thesis.videoservice.config.ApiException
import com.zlrx.thesis.videoservice.config.Messages.VIDEO_NOT_FOUND
import com.zlrx.thesis.videoservice.domain.VideoMetadata
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.http.HttpStatus

interface VideoMetadataRepository : MongoRepository<VideoMetadata, String>, CustomVideoMetaRepository {

    fun findAllByCreatedBy(userId: String, page: PageRequest): Page<VideoMetadata>

    fun findOneByIdAndCreatedBy(id: String, userId: String): VideoMetadata?

    fun findAllByCreatedByAndArchivedFalse(
        userId: String,
        page: PageRequest
    ): Page<VideoMetadata>

    fun findAllByPublicTrueAndArchivedFalseAndCreatedByIsNotAndTenantId(
        userId: String,
        tenantId: String,
        page: PageRequest
    ): Page<VideoMetadata>
}

interface CustomVideoMetaRepository {
    fun getAllAvailableVideos(userId: String): List<VideoMetadata>
}

class CustomVideoMetaRepositoryImpl(
    private val mongoTemplate: MongoTemplate
) : CustomVideoMetaRepository {

    override fun getAllAvailableVideos(userId: String): List<VideoMetadata> {
        val query = Query.query(
            Criteria.where("archived").isEqualTo(false).orOperator(
                Criteria.where("createdBy").isEqualTo(userId),
                Criteria.where("public").isEqualTo(true)
            )
        )
        return mongoTemplate.find(query, VideoMetadata::class.java)
    }
}

fun VideoMetadataRepository.findByIdOrThrow(id: String): VideoMetadata =
    this.findById(id).orElseThrow {
        ApiException(VIDEO_NOT_FOUND, HttpStatus.NOT_FOUND, id)
    }
