package com.nutritrack.nutritrackbackend.dto.response.recipe;

import com.nutritrack.nutritrackbackend.dto.response.allergen.AllergenResponse;
import com.nutritrack.nutritrackbackend.enums.MealType;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeResponse {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private MealType mealType;

    private List<RecipeIngredientResponse> ingredients;

    private Double calories;
    private Double protein;
    private Double fat;
    private Double carbs;

    private Set<AllergenResponse> allergens;
    private String createdBy;
    private int favoritesCount;
}

