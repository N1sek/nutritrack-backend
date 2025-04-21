package com.nutritrack.nutritrackbackend.controller;

import com.nutritrack.nutritrackbackend.dto.request.user.UpdateProfileRequest;
import com.nutritrack.nutritrackbackend.dto.response.user.UserResponse;
import com.nutritrack.nutritrackbackend.entity.User;
import com.nutritrack.nutritrackbackend.security.UserDetailsAdapter;
import com.nutritrack.nutritrackbackend.service.UserService;
import com.nutritrack.nutritrackbackend.mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        User user = ((UserDetailsAdapter) authentication.getPrincipal()).getUser();
        UserResponse response = userMapper.toResponse(user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<Void> updateProfile(
            Authentication authentication,
            @RequestBody @Valid UpdateProfileRequest request
    ) {
        User user = ((UserDetailsAdapter) authentication.getPrincipal()).getUser();
        userService.updateUserProfile(user, request);
        return ResponseEntity.noContent().build();
    }
}
