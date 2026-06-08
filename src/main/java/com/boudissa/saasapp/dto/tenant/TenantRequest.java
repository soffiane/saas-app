package com.boudissa.saasapp.dto.tenant;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TenantRequest {

    @NotBlank(message = "Company name is required")
    private String companyName;
    @NotBlank(message = "Company code is required")
    private String companyCode;
    @NotBlank(message = "Email should not be empty")
    private String email;
    @NotBlank(message = "Admin name should not be empty")
    private String adminName;
    @NotBlank(message = "Admin email should not be empty")
    private String adminEmail;
    @NotBlank(message = "Admin username should not be empty")
    private String adminUsername;
    @NotBlank(message = "Admin password should not be empty")
    private String adminPassword;
}
