package com.nutritrack.nutritrackbackend.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nutritrack.nutritrackbackend.enums.ActivityLevel;
import com.nutritrack.nutritrackbackend.enums.Goal;
import com.nutritrack.nutritrackbackend.validation.MinAge;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank
    private String name;

    @NotBlank
    @Size(min = 4, max = 16)
    private String nickname;

    @Email(message = "Por favor, proporciona un email correcto")
    @NotBlank
    private String email;

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "La password debe contener al menos 8 caracteres, incluyendo mayúscula, minúscula y número"
    )
    @NotBlank
    private String password;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha debe ser en el pasado")
    @MinAge(value = 12, message = "Debes tener al menos 12 años")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private Double height;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private Double weight;

    @NotNull
    private Goal goal;

    @NotNull
    private ActivityLevel activityLevel;

    @NotNull
    private List<Long> allergenIds;
}
