package com.zlrx.thesis.authservice.repository

import com.zlrx.thesis.authservice.config.ApiException
import com.zlrx.thesis.authservice.config.Messages
import com.zlrx.thesis.authservice.domain.Tenant
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.http.HttpStatus

interface TenantRepository : MongoRepository<Tenant, String> {
    fun findByName(name: String): Tenant?
}

fun TenantRepository.findByNameOrThrow(name: String): Tenant =
    this.findByName(name) ?: throw ApiException(Messages.TENANT_NOT_FOUND, HttpStatus.NOT_FOUND)
