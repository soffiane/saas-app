package com.boudissa.saasapp.config;

import com.boudissa.saasapp.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TenantSchemaResolver tenantSchemaResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().contains("/api/v1/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String token = getJwtFromRequest(request);
            if (token != null && jwtService.validateToken(token)) {
                String user = jwtService.getUserIdFromToken(token);
                String role = jwtService.getRoleFromToken(token);
                String tenantId = jwtService.getTenantIdFromToken(token);
                if (tenantId != null) {
                    TenantContext.setCurrentTenant(tenantId);
                    TenantContext.setCurrentSchemma(tenantSchemaResolver.resolveSchemaName(tenantId));
                }
                //create authentication object
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.singletonList(authority));
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Authentication Success for User {}, tenant {}, role {}", user, tenantId, role);
            }
            //on poursuit TOUJOURS la chaine: les endpoints publics (register, login...) n'ont pas de token
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Authentication Failed", e);
        } finally {
            TenantContext.clear();
        }
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        final String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }
}
