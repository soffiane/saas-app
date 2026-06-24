package com.boudissa.saasapp.services.impl;

import com.boudissa.saasapp.config.TenantContext;
import com.boudissa.saasapp.dto.mapper.UserMapper;
import com.boudissa.saasapp.dto.user.UserRequest;
import com.boudissa.saasapp.dto.user.UserResponse;
import com.boudissa.saasapp.entities.Tenant;
import com.boudissa.saasapp.entities.User;
import com.boudissa.saasapp.entities.UserRole;
import com.boudissa.saasapp.exception.DuplicateResourceException;
import com.boudissa.saasapp.exception.ResourcesNotFoundException;
import com.boudissa.saasapp.exception.UnauthorizedException;
import com.boudissa.saasapp.repositories.TenantRepository;
import com.boudissa.saasapp.repositories.UserRepository;
import com.boudissa.saasapp.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TenantRepository tenantRepository;

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
    }

    @Override
    public void create(UserRequest request) {
        String tenantId = TenantContext.getCurrentTenant();
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourcesNotFoundException("Tenant not found"));

        checkIfUserAlreadyExists(request);

        final User user = userMapper.toEntity(request);
        user.setTenant(tenant);
        userRepository.save(user);
        log.info("User created successfully");
    }

    @Override
    public void update(String id, UserRequest request) {
        String tenantId = TenantContext.getCurrentTenant();
        User user = findUserById(id);
        //on verifie si l'utilisateur appartient au tenant actuel
        if(!tenantId.equals(user.getTenant().getId())){
            throw new UnauthorizedException("User not found");
        }
        //il faut verifier que le nouveau username n'existe pas
        if(!request.getUsername().equals(user.getUsername()) && userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new DuplicateResourceException("User already exists");
        }
        //il faut verifier que le nouveau email n'existe pas
        if(!request.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateResourceException("User already exists");
        }
        //verifier le role (on peut pas creer ou modifier un admin comme ca)
        if(UserRole.ROLE_ADMIN.equals(request.getRole())){
            throw new UnauthorizedException("User role is not allowed");
        }
        //maj du user
        final User updatedUser = userMapper.toEntity(request);
        updatedUser.setId(id);
        updatedUser.setTenant(user.getTenant());
        updatedUser.setEnabled(user.isEnabled());
        userRepository.save(updatedUser);
        log.info("User updated successfully");
    }

    @Override
    public void delete(String id) {
        User user = userRepository.findNonDeletedUserById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("User not found"));
        user.setDeleted(true);
        userRepository.save(user);
        log.info("User deleted successfully");
    }

    @Override
    public Page<UserResponse> findAll(int page, int size) {
        String tenantId = TenantContext.getCurrentTenant();

        return userRepository.findAllByTenantId(tenantId, PageRequest.of(page, size))
                .map(userMapper::toResponse);
    }

    @Override
    public UserResponse findById(String id) {
        return userMapper.toResponse(userRepository.findNonDeletedUserById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("User not found")));
    }

    @Override
    public void enable(String id) {
        String tenantId = TenantContext.getCurrentTenant();
        User user = findUserById(id);
        if(!tenantId.equals(user.getTenant().getId())){
            throw new UnauthorizedException("User not found");
        }
        user.setEnabled(true);
        userRepository.save(user);
        log.info("User enabled successfully");
    }

    @Override
    public void disable(String id) {
        String tenantId = TenantContext.getCurrentTenant();
        User user = findUserById(id);
        if(!tenantId.equals(user.getTenant().getId())){
            throw new UnauthorizedException("User not found");
        }
        user.setEnabled(false);
        userRepository.save(user);
        log.info("User disabled successfully");
    }

    private void checkIfUserAlreadyExists(UserRequest user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new DuplicateResourceException("User already exists");
        }
        if(userRepository.existsByEmail(user.getEmail())){
            throw new DuplicateResourceException("User already exists");
        }
    }

    private User findUserById(String id) {
        return userRepository.findNonDeletedUserById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("User not found"));
    }
}
