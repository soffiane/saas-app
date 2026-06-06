package com.boudissa.saasapp.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Intercepte chaque requete HTTP pour identifier le tenant
 * Il s'execute avant les controllers et les services
 *
 * Si aucun tenant_id n'est fourni, la requete est refusée
 */

@Component
//ordre d'execution du filtre
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantFilter implements Filter {

    private static final String TENANT_ID_HEADER = "X-Tenant-Id";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        final String tenantId = resolveTenantId(httpServletRequest);
        if (tenantId == null || tenantId.isBlank()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            httpServletResponse.setContentType("application/json");
            httpServletResponse.getWriter().write("Tenant id is missing, please add it to the header X-Tenant-Id");
            return;
        }
        try {
            TenantContext.setCurrentTenant(tenantId);
            chain.doFilter(httpServletRequest, httpServletResponse);
        } finally {
            TenantContext.clear();
        }
    }

    private String resolveTenantId(final HttpServletRequest request) {
        final String tenantId = request.getHeader(TENANT_ID_HEADER);
        if (tenantId != null && !tenantId.isEmpty()) {
            return tenantId.trim().toLowerCase();
        }
        return null;
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
