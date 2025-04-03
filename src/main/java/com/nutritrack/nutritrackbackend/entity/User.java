package com.nutritrack.nutritrackbackend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nutritrack.nutritrackbackend.enums.ActivityLevel;
import com.nutritrack.nutritrackbackend.enums.Goal;
import com.nutritrack.nutritrackbackend.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Column(unique = true)
    @NotBlank
    @Size(min = 4, max = 16)
    private String nickname;

    @Column(unique = true, nullable = false)
    @Email(message = "Por favor, proporciona un email correcto")
    private String email;

    @Column(nullable = false)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "La password debe contener al menos 8 caracteres, incluyendo mayuscula, minuscula y numero"
    )
    private String password;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha debe ser en el pasado")
    private LocalDate birthDate;

    @NotNull(message = "La altura es obligatoria")
    @DecimalMin(value = "0.0", inclusive = false, message = "La altura debe ser mayor que cero")
    private Double height;

    @NotNull(message = "El peso es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El peso debe ser mayor que cero")
    private Double weight;


    @NotNull(message = "El objetivo es obligatorio")
    @Enumerated(EnumType.STRING)
    private Goal goal;

    @NotNull(message = "El nivel de actividad es obligatorio")
    @Enumerated(EnumType.STRING)
    private ActivityLevel activityLevel;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime lastNicknameChange;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_allergen",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "allergen_id")
    )
    private Set<Allergen> allergens;
}
