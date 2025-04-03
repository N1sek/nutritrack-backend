package com.nutritrack.nutritrackbackend.service.impl;

import com.nutritrack.nutritrackbackend.dto.request.auth.LoginRequest;
import com.nutritrack.nutritrackbackend.dto.request.auth.RegisterRequest;
import com.nutritrack.nutritrackbackend.dto.response.auth.LoginResponse;
import com.nutritrack.nutritrackbackend.dto.response.auth.RegisterResponse;
import com.nutritrack.nutritrackbackend.entity.User;
import com.nutritrack.nutritrackbackend.enums.Role;
import com.nutritrack.nutritrackbackend.repository.UserRepository;
import com.nutritrack.nutritrackbackend.security.JwtService;
import com.nutritrack.nutritrackbackend.service.AllergenService;
import com.nutritrack.nutritrackbackend.service.AuthService;
import com.nutritrack.nutritrackbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final AllergenService allergenService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (userService.emailExists(request.getEmail())) {
            throw new IllegalArgumentException("El email ya esta en uso");
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("Ese nickname ya esta en uso");
        }

        User user = User.builder()
                .name(request.getName())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .birthDate(request.getBirthDate())
                .height(request.getHeight())
                .weight(request.getWeight())
                .goal(request.getGoal())
                .activityLevel(request.getActivityLevel())
                .role(Role.USER)
                .allergens(
                        request.getAllergenIds() == null || request.getAllergenIds().isEmpty()
                                ? Set.of()
                                : Set.copyOf(allergenService.findAllByIds(request.getAllergenIds()))
                )
                .build();

        userService.save(user);

        String jwtToken = jwtService.generateToken(user);

        return RegisterResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales invalidas"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Credenciales invalidas");
        }

        String token = jwtService.generateToken(user);
        return LoginResponse.builder().token(token).build();
    }


}
