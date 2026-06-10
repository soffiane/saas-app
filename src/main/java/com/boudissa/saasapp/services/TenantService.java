package com.boudissa.saasapp.services;

import com.boudissa.saasapp.dto.tenant.TenantRequest;
import com.boudissa.saasapp.dto.tenant.TenantResponse;
import org.springframework.data.domain.Page;

public interface TenantService {

    TenantResponse registerTenant(TenantRequest tenant);

    void approveTenant(String tenantId);

    void disableTenant(String tenantId);

    void activateTenant(String tenantId);

    void suspendTenant(String tenantId);

    Page<TenantResponse> findAllTenants(int page, int size);

}
