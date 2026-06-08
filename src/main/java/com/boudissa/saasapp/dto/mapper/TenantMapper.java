package com.boudissa.saasapp.dto.mapper;

import com.boudissa.saasapp.dto.tenant.TenantRequest;
import com.boudissa.saasapp.dto.tenant.TenantResponse;
import com.boudissa.saasapp.entities.Tenant;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TenantMapper {

    public Tenant toEntity(TenantRequest tenantRequest) {
        return Tenant.builder()
                .companyName(tenantRequest.getCompanyName())
                .companyName(tenantRequest.getCompanyCode())
                .createdAt(LocalDateTime.now())
                .adminEmail(tenantRequest.getAdminEmail())
                .adminUsername(tenantRequest.getAdminUsername())
                .adminName(tenantRequest.getAdminName())
                .build();
    }

    public TenantResponse toResponse(Tenant tenant) {
        return TenantResponse.builder()
                .tenantId(tenant.getId())
                .companyName(tenant.getCompanyName())
                .companyName(tenant.getCompanyCode())
                .createdAt(tenant.getCreatedAt())
                .adminEmail(tenant.getAdminEmail())
                .adminUsername(tenant.getAdminUsername())
                .adminName(tenant.getAdminName())
                .build();
    }
}
