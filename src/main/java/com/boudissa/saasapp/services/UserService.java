package com.boudissa.saasapp.services;

import com.boudissa.saasapp.dto.user.UserRequest;
import com.boudissa.saasapp.dto.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    void create(UserRequest request);

    void update(String id, UserRequest request);

    void delete(String id);

    Page<UserResponse> findAll(int page, int size);

    UserResponse findById(String id);

    void enable(String id);

    void disable(String id);
}
