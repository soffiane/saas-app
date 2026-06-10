package com.boudissa.saasapp.config;

import com.boudissa.saasapp.entities.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
public class AuditingConfig {

    private static final String SYSTEM_AUDITOR = "system";

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
                // No authenticated user (e.g. self-service registration, startup):
                // fall back to a non-null system auditor so NOT NULL created_by/updated_by holds.
                return Optional.of(SYSTEM_AUDITOR);
            }
            final Object principal = authentication.getPrincipal();
            if (principal instanceof User user) {
                return Optional.of(user.getId());
            }
            // JwtAuthenticationFilter stores the user id (String) as the principal.
            if (principal instanceof String userId && !userId.isBlank()) {
                return Optional.of(userId);
            }
            return Optional.of(SYSTEM_AUDITOR);
        };
    }
}
