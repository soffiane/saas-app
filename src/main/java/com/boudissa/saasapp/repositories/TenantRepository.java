package com.boudissa.saasapp.repositories;

import com.boudissa.saasapp.entities.Tenant;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, String> {
    boolean existsByCompanyCode(@NotBlank(message = "Company code is required") String companyCode);

    boolean existsByEmail(@NotBlank(message = "Email should not be empty") String email);
}
