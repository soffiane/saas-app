package com.boudissa.saasapp.repositories;

import com.boudissa.saasapp.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String adminUsername);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.id = :id and u.deleted = false")
    Optional<User> findNonDeletedUserById(String id);

    @Query("SELECT u FROM User u WHERE u.tenant.id = :tenantId and u.deleted = false")
    Page<User> findAllByTenantId(String tenantId, Pageable pageable);
}
