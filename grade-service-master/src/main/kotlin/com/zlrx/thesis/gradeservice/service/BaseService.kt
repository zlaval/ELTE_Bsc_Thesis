package com.zlrx.thesis.gradeservice.service

import com.zlrx.thesis.gradeservice.config.authInfo
import com.zlrx.thesis.gradeservice.config.orBlow

interface UserProvider {

    fun getUser() = authInfo().orBlow()

    fun getUserOrNull() = authInfo()

    fun getTenantId() = authInfo().orBlow().tenantId

    fun getUserId() = authInfo().orBlow().id
}

interface InjectableUserProvider : UserProvider

object InjectableUserProviderImpl : InjectableUserProvider

abstract class BaseService(
    private val userProvider: UserProvider
) : UserProvider by userProvider
