package com.nutritrack.nutritrackbackend.service;

import com.nutritrack.nutritrackbackend.dto.request.auth.LoginRequest;
import com.nutritrack.nutritrackbackend.dto.request.auth.RegisterRequest;
import com.nutritrack.nutritrackbackend.dto.response.auth.LoginResponse;
import com.nutritrack.nutritrackbackend.dto.response.auth.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);

}
