package com.nutritrack.nutritrackbackend.mapper;

import com.nutritrack.nutritrackbackend.dto.response.user.UserResponse;
import com.nutritrack.nutritrackbackend.entity.Allergen;
import com.nutritrack.nutritrackbackend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .role(user.getRole())
                .height(user.getHeight())
                .weight(user.getWeight())
                .goal(user.getGoal())
                .activityLevel(user.getActivityLevel())
                .birthDate(user.getBirthDate())
                .isActive(user.getIsActive())
                .allergenIds(user.getAllergens().stream()
                        .map(Allergen::getId)
                        .collect(Collectors.toSet()))
                .avatarUrl(user.getAvatarUrl())

                .build();
    }
}
