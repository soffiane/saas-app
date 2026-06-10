package com.boudissa.saasapp.config;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.MultiTenancySettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Il faut donner la capacité a Spring et Hibernate de savoir sur quel schema travailler
 */
@Component
@Slf4j
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver<String>, HibernatePropertiesCustomizer {
    @Override
    public String resolveCurrentTenantIdentifier() {
        String currentSchema = TenantContext.getCurrentSchema();
        log.debug("Current schema: {}", currentSchema);
        // Hibernate requires a non-null tenant identifier when multi-tenancy is enabled.
        // Fall back to the public schema when no tenant is set (startup, login, async threads, ...).
        if (currentSchema == null || currentSchema.isBlank()) {
            return TenantSchemaResolver.PUBLIC_SCHEMA;
        }
        return currentSchema;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

    @Override
    public void customize(@NonNull Map<String, Object> hibernateProperties) {
        hibernateProperties.put(MultiTenancySettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
    }
}
