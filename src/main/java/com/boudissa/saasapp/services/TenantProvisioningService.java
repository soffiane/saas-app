package com.boudissa.saasapp.services;

import com.boudissa.saasapp.entities.Tenant;

public interface TenantProvisioningService {

    void provisionTenant(Tenant tenant);
}
