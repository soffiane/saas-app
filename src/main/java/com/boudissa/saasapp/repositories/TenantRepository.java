package com.boudissa.saasapp.repositories;

import com.boudissa.saasapp.entities.Tenant;
import com.boudissa.saasapp.entities.TenantStatus;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, String> {
    boolean existsByCompanyCode(@NotBlank(message = "Company code is required") String companyCode);

    boolean existsByEmail(@NotBlank(message = "Email should not be empty") String email);

    Page<Tenant> findAllByStatus(TenantStatus status, Pageable pageable);
}
