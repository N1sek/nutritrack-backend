package com.nutritrack.nutritrackbackend.service.impl;

import com.nutritrack.nutritrackbackend.dto.request.user.UpdateProfileRequest;
import com.nutritrack.nutritrackbackend.entity.Allergen;
import com.nutritrack.nutritrackbackend.entity.User;
import com.nutritrack.nutritrackbackend.repository.UserRepository;
import com.nutritrack.nutritrackbackend.security.UserDetailsAdapter;
import com.nutritrack.nutritrackbackend.service.AllergenService;
import com.nutritrack.nutritrackbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AllergenService allergenService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return new UserDetailsAdapter(user);
    }

    @Override
    @Transactional
    public void updateUserProfile(User user, UpdateProfileRequest request) {
        if (request.getNickname() != null && !request.getNickname().equals(user.getNickname())) {

            if (userRepository.existsByNickname(request.getNickname())) {
                throw new IllegalArgumentException("Ese nickname ya esta en uso");
            }

            if (user.getLastNicknameChange() == null ||
                    user.getLastNicknameChange().isBefore(LocalDateTime.now().minusDays(30))) {
                user.setNickname(request.getNickname());
                user.setLastNicknameChange(LocalDateTime.now());
            } else {
                throw new IllegalArgumentException("Solo puedes cambiar el nickname una vez cada 30 dias");
            }
        }

        if (request.getHeight() != null) user.setHeight(request.getHeight());
        if (request.getWeight() != null) user.setWeight(request.getWeight());
        if (request.getGoal() != null) user.setGoal(request.getGoal());
        if (request.getActivityLevel() != null) user.setActivityLevel(request.getActivityLevel());
        if (request.getBirthDate() != null) user.setBirthDate(request.getBirthDate());

        if (request.getAllergenIds() != null) {
            List<Allergen> allergens = allergenService.findAllByIds(request.getAllergenIds());
            user.setAllergens(Set.copyOf(allergens));
        }

        if (request.getNewPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        userRepository.save(user);
    }



}
