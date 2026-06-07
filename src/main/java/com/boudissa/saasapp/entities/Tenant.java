package com.boudissa.saasapp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "tenants")
public class Tenant extends AbstractEntity {

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "company_code", nullable = false, unique = true)
    private String companyCode;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TenantStatus status = TenantStatus.PENDING;

    //initial admin credential
    @Column(name = "admin_name", nullable = false)
    private String adminName;
    @Column(name = "admin_email", nullable = false, unique = true)
    private String adminEmail;
    @Column(name = "admin_username", nullable = false, unique = true)
    private String adminUsername;
    @Column(name = "admin_password", nullable = false)
    private String adminPassword;

    /*@OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users;*/
}
