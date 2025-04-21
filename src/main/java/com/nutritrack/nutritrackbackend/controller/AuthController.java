package com.nutritrack.nutritrackbackend.controller;

import com.nutritrack.nutritrackbackend.dto.request.auth.LoginRequest;
import com.nutritrack.nutritrackbackend.dto.request.auth.RegisterRequest;
import com.nutritrack.nutritrackbackend.dto.response.auth.LoginResponse;
import com.nutritrack.nutritrackbackend.dto.response.auth.RegisterResponse;
import com.nutritrack.nutritrackbackend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }


}