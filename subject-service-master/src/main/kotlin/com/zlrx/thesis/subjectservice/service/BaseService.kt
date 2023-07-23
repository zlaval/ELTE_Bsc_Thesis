package com.zlrx.thesis.subjectservice.service

import com.zlrx.thesis.subjectservice.config.authInfo
import com.zlrx.thesis.subjectservice.config.orBlow
import jakarta.persistence.EntityManager
import org.hibernate.Session
import org.springframework.beans.factory.annotation.Autowired

interface UserProvider {

    fun getUser() = authInfo().orBlow()

    fun getUserOrNull() = authInfo()

    fun getTenantId() = authInfo().orBlow().tenantId

    fun getUserId() = authInfo().orBlow().id
}

object UserProviderImpl : UserProvider

abstract class BaseService(
    private val userProvider: UserProvider = UserProviderImpl
) : UserProvider by userProvider {

    @Autowired(required = true)
    protected lateinit var entityManager: EntityManager

    fun <T> withUserFilter(query: () -> T): T {
        val session = entityManager.unwrap(Session::class.java)
        val filter = session.enableFilter("creatorFilter")
        filter.setParameter("userId", getUserId())
        val result = query()
        session.disableFilter("creatorFilter")
        return result
    }
}
