package com.nutritrack.nutritrackbackend.dto.request.user;

import com.nutritrack.nutritrackbackend.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRoleRequest {
    @NotNull
    private Role role;
}
