package com.nutritrack.nutritrackbackend.dto.request.recipe;

import com.nutritrack.nutritrackbackend.dto.response.recipe.RecipeIngredientResponse;
import com.nutritrack.nutritrackbackend.enums.MealType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeRequest {

    @NotBlank
    private String name;

    private String description;

    private String imageUrl;

    @NotNull
    private MealType mealType;

    @NotBlank
    private List<RecipeIngredientResponse> ingredients; // alimentos y cantidad
}
