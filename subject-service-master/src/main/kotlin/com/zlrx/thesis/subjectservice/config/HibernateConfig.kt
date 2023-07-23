package com.zlrx.thesis.subjectservice.config

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule
import org.hibernate.cfg.AvailableSettings
import org.hibernate.context.spi.CurrentTenantIdentifierResolver
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Configuration
class HibernateConfig {

    @Bean
    fun hibernate5Module(): Module = Hibernate5JakartaModule()
}

@Component
open class JpaPropertyCustomizer(
    private val tenantIdResolver: TenantIdentifierResolver
) : HibernatePropertiesCustomizer {
    override fun customize(hibernateProperties: MutableMap<String, Any>) {
        hibernateProperties[AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER] = tenantIdResolver
    }
}

@Component
open class TenantIdentifierResolver : CurrentTenantIdentifierResolver {
    override fun resolveCurrentTenantIdentifier(): String = authInfo()?.tenantId ?: "base"

    override fun validateExistingCurrentSessions(): Boolean = false
}
