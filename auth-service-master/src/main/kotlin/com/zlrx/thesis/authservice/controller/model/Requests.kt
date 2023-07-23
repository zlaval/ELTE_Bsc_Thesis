package com.zlrx.thesis.authservice.controller.model

import com.zlrx.thesis.authservice.controller.validation.PasswordMatch
import com.zlrx.thesis.authservice.controller.validation.ValidOrNullPassword
import com.zlrx.thesis.authservice.controller.validation.ValidPassword
import com.zlrx.thesis.authservice.domain.Role
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.jetbrains.annotations.NotNull

@PasswordMatch
data class RegisterRequest(

    @get:NotNull
    @get:NotBlank
    @get:Size(min = 2)
    val tenant: String,

    @get:NotNull
    @get:NotBlank
    val displayName: String,

    @get:NotNull
    @get:NotBlank
    @get:Size(min = 2)
    val name: String,

    @get:Email
    val email: String,

    @ValidPassword
    val password: String,

    @ValidPassword
    val confirmPassword: String
)

data class LoginRequest(

    @get:Email
    val email: String,

    val password: String,

    @get:NotNull
    @get:NotBlank
    @get:Size(min = 2)
    val tenantName: String,
)

@PasswordMatch
data class PasswordChangeRequest(

    @get:NotNull
    @get:NotBlank
    val currentPassword: String,

    @ValidPassword
    val password: String,

    @ValidPassword
    val confirmPassword: String,
)

@PasswordMatch
data class UserRequest(

    val id: String? = null,

    @get:NotNull
    @get:NotBlank
    @get:Size(min = 2)
    val name: String,

    @get:Email
    val email: String,

    @ValidOrNullPassword
    var password: String?,

    @ValidOrNullPassword
    val confirmPassword: String?,

    @get:NotNull
    val role: Role
)
