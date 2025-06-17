package com.nutritrack.nutritrackbackend.service.impl;

import com.nutritrack.nutritrackbackend.dto.request.user.ChangePasswordRequest;
import com.nutritrack.nutritrackbackend.dto.request.user.UpdateProfileRequest;
import com.nutritrack.nutritrackbackend.dto.response.user.UserResponse;
import com.nutritrack.nutritrackbackend.entity.Allergen;
import com.nutritrack.nutritrackbackend.entity.Recipe;
import com.nutritrack.nutritrackbackend.entity.User;
import com.nutritrack.nutritrackbackend.enums.Role;
import com.nutritrack.nutritrackbackend.mapper.UserMapper;
import com.nutritrack.nutritrackbackend.repository.DailyLogRepository;
import com.nutritrack.nutritrackbackend.repository.FoodRepository;
import com.nutritrack.nutritrackbackend.repository.RecipeRepository;
import com.nutritrack.nutritrackbackend.repository.UserRepository;
import com.nutritrack.nutritrackbackend.security.UserDetailsAdapter;
import com.nutritrack.nutritrackbackend.service.AllergenService;
import com.nutritrack.nutritrackbackend.service.ImageStorageService;
import com.nutritrack.nutritrackbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AllergenService allergenService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    private static final String IMAGE_DIR = "./uploads/images";
    private static final String AVATAR_SUBDIR = "avatars";
    private final ImageStorageService imageStorageService;
    private final FoodRepository foodRepository;
    private final RecipeRepository recipeRepository;
    private final DailyLogRepository dailyLogRepository;


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

        if (!user.getIsActive()) {
            throw new UsernameNotFoundException("Cuenta desactivada");
        }

        return new UserDetailsAdapter(user);
    }

    @Override
    public Optional<User> findByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }


    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        removeUserFromRecipeFavorites(user);
        dailyLogRepository.deleteAllByUser(user);
        foodRepository.deleteAllByCreatedBy(user);
        recipeRepository.deleteAllByCreatedBy(user);
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void deleteMyAccount(User user) {
        removeUserFromRecipeFavorites(user);
        dailyLogRepository.deleteAllByUser(user);
        foodRepository.deleteAllByCreatedBy(user);
        recipeRepository.deleteAllByCreatedBy(user);
        userRepository.delete(user);
    }


    @Override
    @Transactional
    public void toggleUserEnabled(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        user.setIsActive(!user.getIsActive());
        userRepository.save(user);
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

    @Override
    @Transactional
    public void updateUserRole(Long id, Role newRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        user.setRole(newRole);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void updateUserAvatar(User user, MultipartFile file) {
        String url = imageStorageService.store(file);

        user.setAvatarUrl(url);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(User user, ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private void removeUserFromRecipeFavorites(User user) {
        // Recupera todas las recetas que tengan al usuario en favoritos
        List<Recipe> recipes = recipeRepository.findAllByFavoritedByContains(user);
        for (Recipe recipe : recipes) {
            recipe.getFavoritedBy().remove(user);
            recipeRepository.save(recipe);
        }
    }
}
