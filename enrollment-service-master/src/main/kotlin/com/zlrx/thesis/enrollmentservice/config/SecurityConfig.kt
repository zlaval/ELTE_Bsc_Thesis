package com.zlrx.thesis.enrollmentservice.config

import com.nimbusds.jose.JWSAlgorithm
import com.zlrx.thesis.enrollmentservice.filters.LoggingFilter
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.nio.charset.StandardCharsets
import javax.crypto.spec.SecretKeySpec

enum class Role {
    ROLE_ADMIN, ROLE_TEACHER, ROLE_STUDENT, ROLE_SYSTEM;

    companion object {
        fun toRole(value: Any?) = when (value) {
            "ROLE_ADMIN" -> ROLE_ADMIN
            "ROLE_TEACHER" -> ROLE_TEACHER
            "ROLE_STUDENT" -> ROLE_STUDENT
            else -> ROLE_SYSTEM
        }
    }
}

enum class TokenField {
    ROLE, TENANT, USER_ID, USER_NAME, EMAIL
}

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties @ConstructorBinding constructor(
    val secretKey: String
)

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity
    ): SecurityFilterChain = http
        .csrf().disable()
        .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        .oauth2ResourceServer {
            it.jwt().jwtAuthenticationConverter { j ->
                val converter = JwtAuthenticationConverter()
                converter.setJwtGrantedAuthoritiesConverter { jwt ->
                    val c = JwtGrantedAuthoritiesConverter()
                    c.setAuthorityPrefix("")
                    c.setAuthoritiesClaimName(TokenField.ROLE.name)
                    c.convert(jwt)
                }
                converter.convert(j)
            }
        }
        .exceptionHandling {
            it.accessDeniedHandler(BearerTokenAccessDeniedHandler())
        }
        .addFilterAfter(LoggingFilter(), SwitchUserFilter::class.java)
        .build()

    @Bean
    fun jwtDecoder(jwtProperties: JwtProperties): JwtDecoder = NimbusJwtDecoder.withSecretKey(
        SecretKeySpec(
            jwtProperties.secretKey.toByteArray(StandardCharsets.UTF_8),
            0,
            jwtProperties.secretKey.length,
            JWSAlgorithm.HS256.name
        )
    ).build()
}

@ControllerAdvice
class GlobalAdvice {
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAuthorizationError(error: AccessDeniedException): ResponseEntity<Void> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
    }
}

data class AuthenticationInfo(
    val id: String,
    val name: String,
    val email: String,
    val tenantId: String,
    val role: Role,
    val token: String?
) {
    fun isAdmin() = role == Role.ROLE_ADMIN
    fun isTeacher() = role == Role.ROLE_TEACHER
    fun isStudent() = role == Role.ROLE_STUDENT
}

fun authInfo(): AuthenticationInfo? {
    val context = SecurityContextHolder.getContext()
    val jwtAuth = context?.authentication as? JwtAuthenticationToken
    val token = jwtAuth?.token?.tokenValue

    return if (jwtAuth != null) {
        jwtAuth.tokenAttributes?.let {
            return AuthenticationInfo(
                id = it[TokenField.USER_ID.name].toString(),
                name = it[TokenField.USER_NAME.name].toString(),
                email = it[TokenField.EMAIL.name].toString(),
                tenantId = it[TokenField.TENANT.name].toString(),
                role = Role.toRole(it[TokenField.ROLE.name]),
                token = token
            )
        }
    } else (context?.authentication as? SystemAuthentication)?.authInfo
}

fun AuthenticationInfo?.orBlow() = this ?: throw AuthenticationCredentialsNotFoundException("Authentication not found")

data class SystemAuthentication(
    val authInfo: AuthenticationInfo
) : AbstractAuthenticationToken(emptyList()) {
    override fun getCredentials() = null

    override fun getPrincipal() = null
}

fun <T> withAuthContext(
    tenantId: String,
    userId: String = SYSTEM,
    body: () -> T
): T {

    val auth = SecurityContextHolder.getContext().authentication
    if (auth != null && auth !is AnonymousAuthenticationToken) {
        return body()
    }

    return try {
        SecurityContextHolder.setContext(
            SecurityContextHolder.createEmptyContext().also {
                it.authentication = SystemAuthentication(
                    AuthenticationInfo(userId, "System", "", tenantId, Role.ROLE_SYSTEM, "")
                )
            }
        )
        body()
    } finally {
        SecurityContextHolder.clearContext()
    }
}
