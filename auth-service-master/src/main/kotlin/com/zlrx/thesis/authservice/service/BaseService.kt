package com.zlrx.thesis.authservice.service

import com.zlrx.thesis.authservice.config.authInfo
import com.zlrx.thesis.authservice.config.orBlow

interface UserProvider {

    fun getUser() = authInfo().orBlow()

    fun getUserOrNull() = authInfo()

    fun getTenantId() = authInfo().orBlow().tenantId

    fun getUserId() = authInfo().orBlow().id
}

object UserProviderImpl : UserProvider

abstract class BaseService(
    private val userProvider: UserProvider = UserProviderImpl
) : UserProvider by userProvider
