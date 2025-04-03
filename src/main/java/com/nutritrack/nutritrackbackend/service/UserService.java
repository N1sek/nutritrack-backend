package com.nutritrack.nutritrackbackend.service;

import com.nutritrack.nutritrackbackend.dto.request.user.UpdateProfileRequest;
import com.nutritrack.nutritrackbackend.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {
    Optional<User> findByEmail(String email);
    boolean emailExists(String email);
    User save(User user);
    void updateUserProfile(User user, UpdateProfileRequest request);

}
