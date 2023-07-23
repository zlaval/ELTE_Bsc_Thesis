package com.zlrx.thesis.authservice.service

import com.zlrx.thesis.authservice.config.ApiException
import com.zlrx.thesis.authservice.config.Messages.INVALID_CURRENT_PASSWORD
import com.zlrx.thesis.authservice.config.Messages.USER_NOT_FOUND
import com.zlrx.thesis.authservice.config.authInfo
import com.zlrx.thesis.authservice.config.orBlow
import com.zlrx.thesis.authservice.controller.model.PasswordChangeRequest
import com.zlrx.thesis.authservice.controller.model.UserRequest
import com.zlrx.thesis.authservice.domain.Role
import com.zlrx.thesis.authservice.domain.User
import com.zlrx.thesis.authservice.repository.UserRepository
import com.zlrx.thesis.authservice.utils.newId
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path

const val PROFILE_DIR = "/data/profile/"

@Service
class UserService(
    private val repository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val resourceLoader: ResourceLoader
) : BaseService() {

    fun getMe(): User {
        val id = authInfo().orBlow().id
        return repository.findById(id).get()
    }

    fun findByEmail(email: String) = repository.findByEmail(email, getTenantId())

    fun findById(id: String): User? {
        return repository.findById(id).orElse(null)
    }

    fun deleteById(id: String) {
        val user = repository.findById(id)
        user.ifPresent {
            if (it.role != Role.ROLE_ADMIN) {
                repository.delete(it)
            }
        }
    }

    fun findAll(): List<User> = repository.findAll()

    fun saveUser(userRequest: UserRequest): User {
        val user = User(
            id = newId(),
            email = userRequest.email,
            name = userRequest.name,
            password = passwordEncoder.encode(userRequest.password),
            role = userRequest.role,
            profilePicture = null
        )
        return repository.save(user)
    }

    fun updateUser(id: String, userRequest: UserRequest): User {
        val user = repository.findById(id).orElseThrow {
            ApiException(USER_NOT_FOUND, HttpStatus.NOT_FOUND, id)
        }

        val pw = if (userRequest.password != null) {
            passwordEncoder.encode(userRequest.password)
        } else {
            user.password
        }

        val updated = user.copy(
            name = userRequest.name,
            email = userRequest.email,
            role = userRequest.role,
            password = pw
        )

        return repository.save(updated)
    }

    fun updatePassword(passwordChangeRequest: PasswordChangeRequest) {
        val user = repository.findById(authInfo().orBlow().id).get()
        val isCurrentPasswordValid = passwordEncoder.matches(passwordChangeRequest.currentPassword, user.password)
        if (!isCurrentPasswordValid) {
            throw ApiException(INVALID_CURRENT_PASSWORD)
        }
        val hash = passwordEncoder.encode(passwordChangeRequest.password)
        repository.save(user.copy(password = hash))
    }

    fun findAllByIdIn(userIds: List<String>) = if (userIds.isEmpty()) {
        emptyList()
    } else {
        repository.findAllById(userIds)
    }

    fun saveProfile(file: MultipartFile?): String? {
        if (file == null) return null
        val fileName = file.originalFilename
        val newFilename = "${newId()}-$fileName"
        checkAndCreateDir()
        file.transferTo(
            Path.of("$PROFILE_DIR$newFilename")
        )
        val user = repository.findById(authInfo().orBlow().id).get()
        repository.save(user.copy(profilePicture = newFilename))
        return newFilename
    }

    fun loadFile(fileName: String): Resource {
        return resourceLoader.getResource("file:$PROFILE_DIR$fileName")
    }

    private fun checkAndCreateDir() {
        val path = Path.of(PROFILE_DIR)
        Files.createDirectories(path)
    }
}
