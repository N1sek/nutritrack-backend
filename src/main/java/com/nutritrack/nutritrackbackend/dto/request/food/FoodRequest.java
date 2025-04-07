package com.nutritrack.nutritrackbackend.dto.request.food;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodRequest {

    @NotBlank
    private String name;

    private String imageUrl;

    @DecimalMin(value = "0.0", inclusive = true)
    private Double calories;

    private Double protein;
    private Double fat;
    private Double carbs;

    private Set<Long> allergenIds;
}

