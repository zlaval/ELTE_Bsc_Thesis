package com.zlrx.thesis.quizservice.repository

import com.zlrx.thesis.quizservice.config.ApiException
import com.zlrx.thesis.quizservice.config.Messages
import com.zlrx.thesis.quizservice.domain.Quiz
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.http.HttpStatus

interface QuizRepository : MongoRepository<Quiz, String> {

    @Query(value = "{'id':  ?0, 'createdBy':  ?1, 'archive': false}")
    fun findMyActiveById(id: String, createdBy: String): Quiz?

    @Query(value = "{'createdBy':  ?0,  'archive': false}")
    fun findAllMyActive(createdBy: String): List<Quiz>

    fun findOneById(id: String): Quiz
}

fun QuizRepository.findByIdAndCreatedByOrThrow(id: String, userId: String): Quiz =
    this.findMyActiveById(id, userId)
        ?: throw ApiException(Messages.NOT_FOUND, HttpStatus.NOT_FOUND, id)

fun QuizRepository.findByIdOrThrow(id: String): Quiz =
    this.findById(id).orElseThrow { throw ApiException(Messages.NOT_FOUND, HttpStatus.NOT_FOUND, id) }
