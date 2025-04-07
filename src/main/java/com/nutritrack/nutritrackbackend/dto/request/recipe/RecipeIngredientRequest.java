package com.nutritrack.nutritrackbackend.dto.request.recipe;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeIngredientRequest {
    @NotNull
    private Long foodId;

    @DecimalMin(value = "0.0", inclusive = false)
    private Double quantityInGrams;
}
