package com.zlrx.thesis.apigateway.configuration

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.security.Key
import java.util.Date


@ConfigurationProperties(prefix = "public")
data class PublicUrls @ConstructorBinding constructor(
    val urls: List<String>
)

@Component
class AuthenticationFilter(
    private val publicRoutes: PublicUrls,
    private val tokenManager: JwtTokenManager
) : GlobalFilter {

    private val logger = KotlinLogging.logger {}

    val secured = { request: ServerHttpRequest -> publicRoutes.urls.none { request.uri.path.contains(it) } }
    val internal = { request: ServerHttpRequest -> request.uri.path.contains("internal") }
    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        if (internal(exchange.request)) {
            exchange.response.statusCode = HttpStatus.NOT_FOUND
            return exchange.response.setComplete()
        } else if (secured(exchange.request)) {
            val authToken =
                exchange.request.headers[HttpHeaders.AUTHORIZATION]?.first() ?: return unauthorized(exchange)
            val token = authToken.substring(7)

            if (!tokenManager.validate(token)) {
                logger.info { "/${exchange.request.method} => ${exchange.request.path} (unauthorized)" }
                return unauthorized(exchange)
            } else {
                val claims = tokenManager.parseToken(token)
                val tenantId = claims["TENANT"]
                val user = claims["USER_ID"]
                logger.info { "/${exchange.request.method} => ${exchange.request.path} by $user on tenant $tenantId  " }
            }
        } else {
            logger.info { "/${exchange.request.method} => ${exchange.request.path} (public)" }
        }
        return chain.filter(exchange)
    }

    private fun unauthorized(exchange: ServerWebExchange): Mono<Void> {
        logger.info { "Unauthorized request from ${exchange.request.remoteAddress?.address?.hostAddress}" }
        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
        return exchange.response.setComplete()
    }

}

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

    fun parseToken(token: String): Claims = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .body

    fun validate(token: String) = parseToken(token).expiration.after(Date())

}
