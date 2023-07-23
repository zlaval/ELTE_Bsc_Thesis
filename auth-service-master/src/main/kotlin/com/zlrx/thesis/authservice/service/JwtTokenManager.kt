package com.zlrx.thesis.authservice.service

import com.zlrx.thesis.authservice.config.TokenField
import com.zlrx.thesis.authservice.domain.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.security.Key
import java.util.Date

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties @ConstructorBinding constructor(
    val secretKey: String,
    val validityInMinutes: String
)

@Component
class JwtTokenManager(
    val jwtProperties: JwtProperties
) {

    private lateinit var key: Key

    @PostConstruct
    fun init() {
        key = Keys.hmacShaKeyFor(jwtProperties.secretKey.toByteArray(StandardCharsets.UTF_8))
    }

    fun generateToken(user: User): String {
        val expiry = jwtProperties.validityInMinutes.toLong()
        return Jwts.builder()
            .setClaims(
                mapOf(
                    TokenField.ROLE.name to user.role,
                    TokenField.TENANT.name to user.tenantId,
                    TokenField.USER_ID.name to user.id,
                    TokenField.USER_NAME.name to user.name,
                    TokenField.EMAIL.name to user.email
                )
            )
            .setSubject(user.email)
            .setIssuedAt(Date())
            .setExpiration(Date(Date().time + (expiry * 60 * 1000)))
            .signWith(key)
            .compact()
    }
}
