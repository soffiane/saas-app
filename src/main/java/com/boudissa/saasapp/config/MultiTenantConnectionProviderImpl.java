package com.boudissa.saasapp.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.MultiTenancySettings;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.spi.DatabaseConnectionInfo;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Hibernate fournit des classes pour gerer le multi tenant
 */

@Component
@Slf4j
@RequiredArgsConstructor
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider<String>, HibernatePropertiesCustomizer {

    private final DataSource dataSource;

    /**
     * Allow-list pattern for valid schema identifiers: must start with a letter
     * or underscore and contain only letters, digits and underscores.
     */
    private static final Pattern VALID_TENANT_IDENTIFIER = Pattern.compile("^[A-Za-z_][A-Za-z0-9_]*$");

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        log.debug("Getting connection for tenant: {}", tenantIdentifier);
        Connection connection = getAnyConnection();
        if (tenantIdentifier != null && !tenantIdentifier.equals("public")) {
            if (!VALID_TENANT_IDENTIFIER.matcher(tenantIdentifier).matches()) {
                connection.close();
                throw new SQLException("Invalid tenant identifier: " + tenantIdentifier);
            }
            //pour choisir le schema il faut faire un set search_path
            //the identifier is validated above; double-quoting prevents SQL injection
            @SuppressWarnings("java:S2077") // tenantIdentifier is allow-list validated above
            String sql = String.format("SET search_path TO \"%s\", public", tenantIdentifier);
            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
                log.debug("Set search_path to {}, public", tenantIdentifier);
            } catch (SQLException e) {
                log.error("Failed to set search_path to {}", tenantIdentifier, e);
                connection.close();
                throw e;
            }
        }
        return connection;
    }

    @Override
    public Connection getReadOnlyConnection(String tenantIdentifier) throws SQLException {
        return MultiTenantConnectionProvider.super.getReadOnlyConnection(tenantIdentifier);
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("SET search_path TO public");
        } catch (SQLException e) {
            connection.close();
            throw e;
        }
        connection.close();
    }

    @Override
    public void releaseReadOnlyConnection(String tenantIdentifier, Connection connection) throws SQLException {
        MultiTenantConnectionProvider.super.releaseReadOnlyConnection(tenantIdentifier, connection);
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean handlesConnectionSchema() {
        return MultiTenantConnectionProvider.super.handlesConnectionSchema();
    }

    @Override
    public boolean handlesConnectionReadOnly() {
        return MultiTenantConnectionProvider.super.handlesConnectionReadOnly();
    }

    @Override
    public DatabaseConnectionInfo getDatabaseConnectionInfo(Dialect dialect) {
        return MultiTenantConnectionProvider.super.getDatabaseConnectionInfo(dialect);
    }

    @Override
    public boolean isUnwrappableAs(Class<?> unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(MultiTenancySettings.MULTI_TENANT_CONNECTION_PROVIDER, this);
    }
}
