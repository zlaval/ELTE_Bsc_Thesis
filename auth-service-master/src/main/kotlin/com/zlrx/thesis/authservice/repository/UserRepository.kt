package com.zlrx.thesis.authservice.repository

import com.zlrx.thesis.authservice.domain.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface UserRepository : MongoRepository<User, String> {

    @Query(value = "{'email':  ?0, 'tenantId':  ?1}")
    fun findByEmail(email: String, tenantId: String): User?
}
