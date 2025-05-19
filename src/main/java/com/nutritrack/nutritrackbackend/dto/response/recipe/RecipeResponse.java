package com.nutritrack.nutritrackbackend.dto.response.recipe;

import com.nutritrack.nutritrackbackend.dto.response.allergen.AllergenResponse;
import com.nutritrack.nutritrackbackend.enums.MealType;
import lombok.*;

import java.time.LocalDateTime;
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
    private String instructions;
    private String imageUrl;

    private MealType mealType;
    private Boolean isPublic;

    private List<RecipeIngredientResponse> ingredients;

    private Double calories;
    private Double protein;
    private Double fat;
    private Double carbs;
    private Double sugar;
    private Double salt;
    private Double saturatedFat;

    private Set<AllergenResponse> allergens;

    private String createdBy;
    private int favoritesCount;
    private boolean isFavorited;

    private LocalDateTime createdAt;
}

