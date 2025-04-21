package com.nutritrack.nutritrackbackend.controller;

import com.nutritrack.nutritrackbackend.dto.request.user.UpdateUserRoleRequest;
import com.nutritrack.nutritrackbackend.dto.response.user.UserResponse;
import com.nutritrack.nutritrackbackend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{id}/toggle")
    public ResponseEntity<Void> toggleUserEnabled(@PathVariable Long id) {
        userService.toggleUserEnabled(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<Void> updateUserRole(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserRoleRequest request
    ) {
        userService.updateUserRole(id, request.getRole());
        return ResponseEntity.noContent().build();
    }


}
