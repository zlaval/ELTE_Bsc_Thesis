package com.zlrx.thesis.controller

import com.zlrx.thesis.domain.User
import io.micronaut.core.version.annotation.Version
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Controller("/user")
@Secured(SecurityRule.IS_ANONYMOUS)
class UserController {

    @Get
    @Version("1")
    fun getUsersV1(): Flow<User> = flow {
        emit(User("zalan", "zlaval@gmail.com"))
    }

}