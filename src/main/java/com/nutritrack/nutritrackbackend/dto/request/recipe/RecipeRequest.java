package com.nutritrack.nutritrackbackend.dto.request.recipe;

import com.nutritrack.nutritrackbackend.enums.MealType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    private String instructions;

    private MealType mealType;

    private String imageUrl;

    private Boolean isPublic;

    @Size(min = 1, message = "La receta debe tener al menos un ingrediente")
    private List<RecipeIngredientRequest> ingredients;
}
