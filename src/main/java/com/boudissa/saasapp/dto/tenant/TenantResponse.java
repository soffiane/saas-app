package com.boudissa.saasapp.dto.tenant;

import com.boudissa.saasapp.entities.TenantStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TenantResponse {

    private String tenantId;
    private String companyName;
    private String companyCode;
    private LocalDateTime createdAt;
    private String email;
    private String adminName;
    private String adminEmail;
    private String adminUsername;
    private String adminPassword;
    private TenantStatus status;
}
