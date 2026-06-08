package com.boudissa.saasapp.services.impl;

import com.boudissa.saasapp.dto.mapper.TenantMapper;
import com.boudissa.saasapp.dto.tenant.TenantRequest;
import com.boudissa.saasapp.dto.tenant.TenantResponse;
import com.boudissa.saasapp.entities.Tenant;
import com.boudissa.saasapp.entities.TenantStatus;
import com.boudissa.saasapp.entities.User;
import com.boudissa.saasapp.entities.UserRole;
import com.boudissa.saasapp.exception.DuplicateResourceException;
import com.boudissa.saasapp.exception.InvalidTenantStateException;
import com.boudissa.saasapp.exception.ResourcesNotFoundException;
import com.boudissa.saasapp.repositories.TenantRepository;
import com.boudissa.saasapp.repositories.UserRepository;
import com.boudissa.saasapp.services.TenantProvisioningService;
import com.boudissa.saasapp.services.TenantService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TenantProvisioningService tenantProvisioningService;

    @Override
    public void registerTenant(TenantRequest tenant) {
        //check if tenant already exists
        if (tenantRepository.existsByCompanyCode(tenant.getCompanyCode())) {
            throw new DuplicateResourceException("Tenant already exists");
        }
        //check if email already exists
        if (tenantRepository.existsByEmail(tenant.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        //create tenant
        final Tenant tenantEntity = tenantMapper.toEntity(tenant);
        tenantEntity.setStatus(TenantStatus.PENDING);
        tenantEntity.setAdminPassword(passwordEncoder.encode(tenant.getAdminPassword()));
        tenantRepository.save(tenantEntity);

    }

    @Override
    public void approveTenant(String tenantId) {
        //verify if tenant exists
        final Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(() -> new ResourcesNotFoundException("Tenant not found"));
        //update tenant status
        tenant.setStatus(TenantStatus.ACTIVE);
        tenantRepository.save(tenant);

        try {
            //provision the schema for tenant
            tenantProvisioningService.provisionTenant(tenant);
            //create initial admin user
            createInitialAdminUser(tenant);

        } catch (Exception e) {
            tenant.setStatus(TenantStatus.PENDING);
            tenantRepository.save(tenant);
            throw e;
        }
    }

    @Override
    public void disableTenant(String tenantId) {
        //verify if tenant exists
        final Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(() -> new ResourcesNotFoundException("Tenant not found"));
        //check if tenant is active
        if (tenant.getStatus() != TenantStatus.ACTIVE) {
            throw new InvalidTenantStateException("Tenant is not approved");
        }
        //update tenant status
        tenant.setStatus(TenantStatus.INACTIVE);
        tenantRepository.save(tenant);
    }

    @Override
    public void activateTenant(String tenantId) {
        //verify if tenant exists
        final Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(() -> new ResourcesNotFoundException("Tenant not found"));
        //check if tenant is pending

        if (tenant.getStatus() != TenantStatus.PENDING) {
            throw new InvalidTenantStateException("Tenant is not approved");
        }
        //update tenant status
        tenant.setStatus(TenantStatus.ACTIVE);
        tenantRepository.save(tenant);
    }

    @Override
    public void suspendTenant(String tenantId) {
        //verify if tenant exists
        final Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(() -> new ResourcesNotFoundException("Tenant not found"));
        //check if tenant is pending
        if (tenant.getStatus() != TenantStatus.ACTIVE) {
            throw new InvalidTenantStateException("Tenant is not approved");
        }
        //update tenant status
        tenant.setStatus(TenantStatus.SUSPENDED);
        tenantRepository.save(tenant);
    }

    @Override
    public void updateTenant(String tenantId, TenantStatus status) {

    }

    @Override
    public Page<TenantResponse> findAllTenants(int page, int size) {
        return tenantRepository.findAll(PageRequest.of(page, size)).map(tenantMapper::toResponse);
    }

    @Override
    public Page<TenantResponse> findAllTenantsByStatus(int page, int size, TenantStatus status) {
        return tenantRepository.findAllByStatus(status, PageRequest.of(page, size)).map(tenantMapper::toResponse);
    }

    @Override
    public TenantResponse findTenantById(String tenantId) {
        return tenantMapper.toResponse(tenantRepository.findById(tenantId).orElseThrow(() -> new ResourcesNotFoundException("Tenant not found")));
    }

    @Override
    public void deleteTenant(String tenantId) {
        if (tenantRepository.findById(tenantId).isPresent()) {
            tenantRepository.deleteById(tenantId);
        }
    }

    private void createInitialAdminUser(Tenant tenant) {
        if (userRepository.existsByUsername(tenant.getAdminUsername())) {
            throw new DuplicateResourceException("Admin user already exists");
        }

        final User admin = User.builder()
                .username(tenant.getAdminUsername())
                .firstName(extractFirstName(tenant.getAdminName()))
                .lastName(extractLastName(tenant.getAdminName()))
                .email(tenant.getAdminEmail())
                .password(passwordEncoder.encode(tenant.getAdminPassword()))
                .role(UserRole.ROLE_ADMIN)
                .tenant(tenant)
                .createdAt(LocalDateTime.now())
                .enabled(true)
                .build();
        userRepository.save(admin);
    }

    private String extractFirstName(String adminName) {
        return adminName.split(" ")[0];
    }

    private String extractLastName(String adminName) {
        return adminName.split(" ")[1].length() > 1 ? adminName.split(" ")[1] : "";
    }
}
