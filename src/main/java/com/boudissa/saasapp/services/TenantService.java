package com.boudissa.saasapp.services;

import com.boudissa.saasapp.dto.tenant.TenantRequest;
import com.boudissa.saasapp.dto.tenant.TenantResponse;
import com.boudissa.saasapp.entities.TenantStatus;
import org.springframework.data.domain.Page;

public interface TenantService {

    void registerTenant(TenantRequest tenant);

    void approveTenant(String tenantId);

    void disableTenant(String tenantId);

    void activateTenant(String tenantId);

    void suspendTenant(String tenantId);

    void updateTenant(String tenantId, TenantStatus status);

    Page<TenantResponse> findAllTenants(int page, int size);

    Page<TenantResponse> findAllTenantsByStatus(int page, int size, TenantStatus status);

    TenantResponse findTenantById(String tenantId);

    void deleteTenant(String tenantId);


}
