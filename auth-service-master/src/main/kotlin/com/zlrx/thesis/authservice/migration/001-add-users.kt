package com.zlrx.thesis.authservice.migration

import com.zlrx.thesis.authservice.config.withAuthContext
import com.zlrx.thesis.authservice.domain.Role
import com.zlrx.thesis.authservice.domain.Tenant
import com.zlrx.thesis.authservice.domain.User
import com.zlrx.thesis.authservice.utils.SYSTEM
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Instant

@ChangeUnit(id = "001-add-users", order = "1", author = "zlaval")
class `001-add-users` {

    @Execution
    fun execution(mongoTemplate: MongoTemplate, passwordEncoder: PasswordEncoder) {

        val tenantsToSave: Collection<Tenant> = listOf(tenant("1", "elte"), tenant("2", "pte"))
        mongoTemplate.insert(tenantsToSave, Tenant::class.java)

        val adminPwd = passwordEncoder.encode("admin")
        val teacherPwd = passwordEncoder.encode("teacher")
        val studentPwd = passwordEncoder.encode("student")
        var id = 1
        tenantsToSave.forEach { tenant ->
            val teachers = (0..2).map { teacher(tenant, teacherPwd, it, id++) }
            val students = (0..4).map { student(tenant, studentPwd, it, id++) }
            val admins = admin(tenant, adminPwd, id++)

            withAuthContext(tenant.id) {
                mongoTemplate.insert(teachers + students + admins, User::class.java)
            }
        }
    }

    private fun student(tenant: Tenant, password: String, index: Int, id: Int) = user().copy(
        id = id.toString(),
        email = "student$index@${tenant.name}.hu",
        name = "${tenant.displayName} Student-$index",
        password = password,
        role = Role.ROLE_STUDENT
    )

    private fun teacher(tenant: Tenant, password: String, index: Int, id: Int) = user().copy(
        id = id.toString(),
        email = "teacher$index@${tenant.name}.hu",
        name = "${tenant.displayName} Teacher-$index",
        password = password,
        role = Role.ROLE_TEACHER
    )

    private fun admin(tenant: Tenant, password: String, id: Int) = user().copy(
        id = id.toString(),
        email = "admin@${tenant.name}.hu",
        name = "${tenant.displayName} Admin",
        password = password,
        role = Role.ROLE_ADMIN
    )

    private fun tenant(id: String, name: String) = Tenant(
        id = id,
        name = name,
        displayName = name.uppercase(),
        version = 1,
        createdAt = Instant.now(),
        modifiedAt = Instant.now(),
        createdBy = SYSTEM,
        modifiedBy = SYSTEM
    )

    private fun user() = User(
        id = "",
        email = "user@email.com",
        name = "Joe Doe",
        password = "password",
        role = Role.ROLE_STUDENT,
        version = 1,
        createdAt = Instant.now(),
        modifiedDate = Instant.now(),
        createdBy = SYSTEM,
        modifiedBy = SYSTEM,
        profilePicture = null
    )

    @RollbackExecution
    fun rollbackExecution() {
    }
}
