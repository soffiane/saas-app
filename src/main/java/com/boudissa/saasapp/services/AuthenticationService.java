package com.boudissa.saasapp.services;

import com.boudissa.saasapp.dto.login.LoginRequest;
import com.boudissa.saasapp.dto.login.LoginResponse;

public interface AuthenticationService {

    LoginResponse login(LoginRequest request);
}
