package com.nutritrack.nutritrackbackend.dto.response.user;

import com.nutritrack.nutritrackbackend.enums.ActivityLevel;
import com.nutritrack.nutritrackbackend.enums.Goal;
import com.nutritrack.nutritrackbackend.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String nickname;
    private String email;
    private LocalDate birthDate;
    private Double height;
    private Double weight;
    private Goal goal;
    private ActivityLevel activityLevel;
    private Role role;
    private Boolean isActive;
    private Set<Long> allergenIds;

}
