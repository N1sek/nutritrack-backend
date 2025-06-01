package com.nutritrack.nutritrackbackend.service;

import com.nutritrack.nutritrackbackend.dto.request.user.UpdateProfileRequest;
import com.nutritrack.nutritrackbackend.dto.response.user.UserResponse;
import com.nutritrack.nutritrackbackend.entity.User;
import com.nutritrack.nutritrackbackend.enums.Role;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    Optional<User> findByEmail(String email);
    boolean emailExists(String email);
    User save(User user);
    void updateUserProfile(User user, UpdateProfileRequest request);
    List<UserResponse> getAllUsers();
    void deleteUserById(Long id);
    void toggleUserEnabled(Long id);
    void updateUserRole(Long id, Role newRole);
    Optional<User> findByNickname(String nickname);

    default User getByEmail(String email) {
        return findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("No se ha encontrado el usuario con el email: " + email));
    }

}
