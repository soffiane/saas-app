package com.boudissa.saasapp.controller;

import com.boudissa.saasapp.dto.login.LoginRequest;
import com.boudissa.saasapp.dto.login.LoginResponse;
import com.boudissa.saasapp.dto.tenant.TenantRequest;
import com.boudissa.saasapp.dto.tenant.TenantResponse;
import com.boudissa.saasapp.services.AuthenticationService;
import com.boudissa.saasapp.services.TenantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller", description = "Authentication API")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final TenantService tenantService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/register")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PLATFORM_ADMIN')")
    public ResponseEntity<TenantResponse> registerTenant(@RequestBody @Valid TenantRequest request) {
        return ResponseEntity.ok(tenantService.registerTenant(request));
    }
}
