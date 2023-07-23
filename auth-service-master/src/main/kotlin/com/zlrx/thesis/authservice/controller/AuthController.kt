package com.zlrx.thesis.authservice.controller

import com.zlrx.thesis.authservice.config.MessageResolver
import com.zlrx.thesis.authservice.config.withAuthContext
import com.zlrx.thesis.authservice.controller.model.LoginRequest
import com.zlrx.thesis.authservice.controller.model.LoginResponse
import com.zlrx.thesis.authservice.controller.model.RegisterRequest
import com.zlrx.thesis.authservice.controller.model.UserRequest
import com.zlrx.thesis.authservice.domain.Role
import com.zlrx.thesis.authservice.domain.Tenant
import com.zlrx.thesis.authservice.repository.TenantRepository
import com.zlrx.thesis.authservice.repository.findByNameOrThrow
import com.zlrx.thesis.authservice.service.JwtTokenManager
import com.zlrx.thesis.authservice.service.UserService
import com.zlrx.thesis.authservice.utils.newId
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class AuthController(
    private val tenantRepository: TenantRepository,
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
    private val tokenProvider: JwtTokenManager,
    private val messageResolver: MessageResolver
) {

    @PostMapping(path = ["/register"])
    fun register(@RequestBody @Valid request: RegisterRequest): ResponseEntity<String> {
        val tenantName = request.tenant.lowercase().filter { it.isLetterOrDigit() }
        val existingTenant = tenantRepository.findByName(tenantName)
        return if (existingTenant != null) {
            ResponseEntity.badRequest().body(messageResolver.getMessage("tenant.already.registered"))
        } else {
            val tenant = tenantRepository.save(Tenant(newId(), tenantName, request.displayName))
            withAuthContext(tenant.id) {
                userService.saveUser(
                    UserRequest(
                        name = request.name,
                        email = request.email,
                        password = request.password,
                        confirmPassword = request.confirmPassword,
                        role = Role.ROLE_ADMIN
                    )
                )
                ResponseEntity.ok().body(tenantName)
            }
        }
    }

    @PostMapping(path = ["/login"])
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<*> {
        val tenant = tenantRepository.findByNameOrThrow(loginRequest.tenantName)
        return withAuthContext(tenant.id) {
            val user = userService.findByEmail(loginRequest.email)
            if (user == null) {
                authFailed()
            } else {
                val passwordCheck = passwordEncoder.matches(loginRequest.password, user.password)
                if (passwordCheck) {
                    ResponseEntity.ok(LoginResponse(tokenProvider.generateToken(user)))
                } else {
                    authFailed()
                }
            }
        }
    }

    private fun authFailed() = ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(messageResolver.getMessage("authentication.failed"))
}
