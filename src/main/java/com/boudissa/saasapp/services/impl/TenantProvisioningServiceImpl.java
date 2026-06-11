package com.boudissa.saasapp.services.impl;

import com.boudissa.saasapp.entities.Tenant;
import com.boudissa.saasapp.exception.TenantProvisioningException;
import com.boudissa.saasapp.services.TenantProvisioningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantProvisioningServiceImpl implements TenantProvisioningService {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    @Override
    public void provisionTenant(Tenant tenant) {
        final String schemaName = tenant.getCompanyCode().toLowerCase();
        try {
            log.info("Provisioning tenant: {}", schemaName);
            //create schema in postgresql
            createSchema(schemaName);
            log.info("Schema created successfully: {}", schemaName);
            //run flyway migration for this schema
            log.info("Running flyway migration for tenant: {}", schemaName);
            runTenantMigration(schemaName);
            log.info("Flyway migration completed for tenant: {}", schemaName);
            //initialize data for this schema (optional)
            log.info("Initializing data for tenant: {}", schemaName);
            initializeData(schemaName);
            log.info("Data initialized for tenant: {}", schemaName);

        } catch (Exception e) {
            log.error("Failed to provision tenant: {}", schemaName, e);
            try {
                //rollback schema creation
                dropSchema(schemaName);
            } catch (Exception ex) {
                log.error("Failed to rollback schema creation for tenant: {}", schemaName, ex);
            }

            throw new TenantProvisioningException("Failed to provision tenant: " + schemaName);
        }
    }

    private void dropSchema(String schemaName) {
        jdbcTemplate.execute(String.format("DROP SCHEMA IF EXISTS %s CASCADE", schemaName));
    }

    private void createSchema(String schemaName) {
        final String sql = String.format("CREATE SCHEMA IF NOT EXISTS %s",schemaName);
        try {
            jdbcTemplate.execute(sql);
        } catch (Exception e) {
            log.error("Failed to create schema: {}", schemaName, e);
            throw new RuntimeException("Failed to create schema: " + schemaName, e);
        }
    }

    private void runTenantMigration(String schemaName) {
        final Flyway tenantFlyway = Flyway.configure()
                .dataSource(dataSource)
                .schemas(schemaName)
                .locations("classpath:/db/migration/tenant")
                .baselineOnMigrate(true)
                .table("schema_history")
                .validateOnMigrate(true)
                .cleanDisabled(true)
                .load();
        log.info("Tenant migration started");
        tenantFlyway.migrate();
        log.info("Tenant migration complete");
    }

    private void initializeData(String schemaName) {
    }
}
