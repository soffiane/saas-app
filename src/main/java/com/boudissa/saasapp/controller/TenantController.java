package com.boudissa.saasapp.controller;

import com.boudissa.saasapp.dto.tenant.TenantResponse;
import com.boudissa.saasapp.services.TenantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_PLATFORM_ADMIN')")
@Tag(name = "Tenant Controller", description = "Tenant API")
public class TenantController {

    private final TenantService tenantService;

    @PatchMapping("/approve/{tenantId}")
    public ResponseEntity<Void> approveTenant(@PathVariable String tenantId) {
        tenantService.approveTenant(tenantId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/disable/{tenantId}")
    public ResponseEntity<Void> disableTenant(@PathVariable String tenantId) {
        tenantService.disableTenant(tenantId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/suspend/{tenantId}")
    public ResponseEntity<Void> suspendTenant(@PathVariable String tenantId) {
        tenantService.suspendTenant(tenantId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/activate/{tenantId}")
    public ResponseEntity<Void> activateTenant(@PathVariable String tenantId) {
        tenantService.activateTenant(tenantId);
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public ResponseEntity<Page<TenantResponse>> findAllTenants(@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(tenantService.findAllTenants(page, size));
    }
}
