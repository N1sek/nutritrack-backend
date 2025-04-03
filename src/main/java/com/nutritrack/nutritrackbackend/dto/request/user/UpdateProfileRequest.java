package com.nutritrack.nutritrackbackend.dto.request.user;

import com.nutritrack.nutritrackbackend.enums.ActivityLevel;
import com.nutritrack.nutritrackbackend.enums.Goal;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class UpdateProfileRequest {

    private String nickname;

    @DecimalMin(value = "0.0", inclusive = false, message = "La altura debe ser mayor que cero")
    private Double height;

    @DecimalMin(value = "0.0", inclusive = false, message = "El peso debe ser mayor que cero")
    private Double weight;

    private Goal goal;

    private ActivityLevel activityLevel;

    @Past(message = "La fecha debe estar en el pasado")
    private LocalDate birthDate;

    private List<Long> allergenIds;

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un número"
    )
    private String newPassword;
}
