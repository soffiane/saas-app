package com.boudissa.saasapp.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TenantSchemaResolver {

    public static final String PUBLIC_SCHEMA = "public";

    private final JdbcTemplate jdbcTemplate;

    @Cacheable(value = "tenants", key = "#tenantId")
    public String resolveSchemaName(String tenantId) {
        if (tenantId == null) {
            return PUBLIC_SCHEMA;
        }
        try {
            final String companyCode = jdbcTemplate.queryForObject("SELECT company_code FROM tenants WHERE id = ? and deleted = false", String.class, tenantId);
            if (companyCode != null) {
                final String schemaName = companyCode.toLowerCase();
                log.debug("Resolved schema name for tenant {} to {}", tenantId, schemaName);
                return schemaName;
            }
        } catch (Exception e) {
            log.error("Error resolving schema name for tenant {}", tenantId, e);
        }
        return PUBLIC_SCHEMA;
    }
}
