package com.zlrx.thesis.videoservice

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.test.context.support.WithSecurityContext
import org.springframework.security.test.context.support.WithSecurityContextFactory
import org.springframework.stereotype.Component
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.lifecycle.Startables

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ContextConfiguration(initializers = [TestContainersInitializer::class])
annotation class WithTestContainers

class TestContainersInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    companion object {

        val mongo = MongoDBContainer("mongo:6.0.4")

        init {
            Startables.deepStart(mongo).join()
        }
    }

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        TestPropertyValues.of(
            "spring.data.mongodb.uri" prop mongo.connectionString,
        ).applyTo(applicationContext.environment)
    }

    private infix fun String.prop(value: Any) = "$this=$value"
}

// tenant 1 id 9
const val TEST_ADMIN_TOKEN =
    "eyJhbGciOiJIUzI1NiJ9.eyJST0xFIjoiUk9MRV9BRE1JTiIsIlRFTkFOVCI6IjEiLCJVU0VSX0lEIjoiOSIsIlVTRVJfTkFNRSI6IkVMVEUgQWRtaW4iLCJFTUFJTCI6ImFkbWluQGVsdGUuaHUiLCJzdWIiOiJhZG1pbkBlbHRlLmh1IiwiaWF0IjoxNjc5MDA0MDEyLCJleHAiOjEwODk2OTg2Njc0OTkyfQ.thIsVYQbADgzdXhBorswIC36F-WB8VJUd2Vov6WR27I"

// tenant 1 id 1
const val TEST_TEACHER_TOKEN =
    "eyJhbGciOiJIUzI1NiJ9.eyJST0xFIjoiUk9MRV9URUFDSEVSIiwiVEVOQU5UIjoiMSIsIlVTRVJfSUQiOiIxIiwiVVNFUl9OQU1FIjoiRUxURSBUZWFjaGVyLTAiLCJFTUFJTCI6InRlYWNoZXIwQGVsdGUuaHUiLCJzdWIiOiJ0ZWFjaGVyMEBlbHRlLmh1IiwiaWF0IjoxNjc5MDA0MDc3LCJleHAiOjEwODk2OTg2Njc1MDU3fQ.YH_PPL7b7WXnvdBljpzqu3OOr2FHpt8eZHFfxOW4NXw"

// tenant 1 id 4
const val TEST_STUDENT_TOKEN =
    "eyJhbGciOiJIUzI1NiJ9.eyJST0xFIjoiUk9MRV9TVFVERU5UIiwiVEVOQU5UIjoiMSIsIlVTRVJfSUQiOiI0IiwiVVNFUl9OQU1FIjoiRUxURSBTdHVkZW50LTAiLCJFTUFJTCI6InN0dWRlbnQwQGVsdGUuaHUiLCJzdWIiOiJzdHVkZW50MEBlbHRlLmh1IiwiaWF0IjoxNjc5MDA0MTIxLCJleHAiOjEwODk2OTg2Njc1MTAxfQ.m5chhLiFFxh_8ud2JWkZy3Dz9eBqLI_axowT3y1SvYM"

@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = WithBearerTokenSecurityFactory::class)
annotation class JwtUser(
    val token: String
)

@JwtUser(TEST_ADMIN_TOKEN)
@Retention(AnnotationRetention.RUNTIME)
annotation class WithAdmin

@JwtUser(TEST_TEACHER_TOKEN)
@Retention(AnnotationRetention.RUNTIME)
annotation class WithTeacher

@JwtUser(TEST_STUDENT_TOKEN)
@Retention(AnnotationRetention.RUNTIME)
annotation class WithStudent

@Component
class WithBearerTokenSecurityFactory(
    private val jwtDecoder: JwtDecoder
) : WithSecurityContextFactory<JwtUser> {
    override fun createSecurityContext(annotation: JwtUser): SecurityContext {
        return SecurityContextHolder.createEmptyContext().also {
            it.authentication = JwtAuthenticationToken(jwtDecoder.decode(annotation.token))
        }
    }
}
