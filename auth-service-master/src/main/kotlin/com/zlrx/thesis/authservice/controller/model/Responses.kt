package com.zlrx.thesis.authservice.controller.model

import com.zlrx.thesis.authservice.domain.Role
import com.zlrx.thesis.authservice.domain.User

data class LoginResponse(
    val token: String
)

data class UserResponse(
    val id: String,
    val email: String,
    val name: String,
    val role: Role,
    val picture: String?
) {
    companion object {
        fun fromUserOrNull(user: User?) = if (user != null) {
            UserResponse(
                user.id,
                user.email,
                user.name,
                user.role,
                user.profilePicture
            )
        } else {
            null
        }

        fun fromUser(user: User) = fromUserOrNull(user)!!
    }
}
