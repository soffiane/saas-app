package com.boudissa.saasapp.services.impl;

import com.boudissa.saasapp.dto.login.LoginRequest;
import com.boudissa.saasapp.dto.login.LoginResponse;
import com.boudissa.saasapp.entities.User;
import com.boudissa.saasapp.security.JwtService;
import com.boudissa.saasapp.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {
        final Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        final User user = (User) authenticate.getPrincipal();
        final String token = jwtService.generateToken(user.getUsername(), user.getTenantId(), user.getRole().name());
        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .build();
    }
}
