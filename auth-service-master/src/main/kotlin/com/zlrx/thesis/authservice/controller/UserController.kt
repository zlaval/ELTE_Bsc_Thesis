package com.zlrx.thesis.authservice.controller

import com.zlrx.thesis.authservice.controller.model.PasswordChangeRequest
import com.zlrx.thesis.authservice.controller.model.UserRequest
import com.zlrx.thesis.authservice.controller.model.UserResponse
import com.zlrx.thesis.authservice.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val service: UserService
) {

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    fun getMe(): UserResponse {
        return UserResponse.fromUser(service.getMe())
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/me/password")
    fun changePassword(
        @RequestBody @Valid
        request: PasswordChangeRequest
    ): ResponseEntity<Void> {
        service.updatePassword(request)
        return ResponseEntity.ok().build()
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/image", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadProfilePicture(
        @RequestPart("picture")
        image: MultipartFile?
    ): String? {
        return service.saveProfile(image)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    fun users(): ResponseEntity<List<UserResponse>> {
        val response = service.findAll()
            .map { UserResponse.fromUser(it) }
        return ResponseEntity.ok(response)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    fun user(@PathVariable id: String): ResponseEntity<UserResponse> {
        val response = UserResponse.fromUserOrNull(
            service.findById(id)
        )
        return ResponseEntity.ok(response)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<Void> {
        service.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    fun save(@RequestBody @Valid user: UserRequest): ResponseEntity<UserResponse> {
        val response = service.saveUser(user)
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.fromUser(response))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    fun update(@PathVariable id: String, @RequestBody @Valid user: UserRequest): ResponseEntity<UserResponse> {
        val response = service.updateUser(id, user)
        return ResponseEntity.ok(UserResponse.fromUser(response))
    }
}
