package com.nutritrack.nutritrackbackend.dto.response.dailylog;

import com.nutritrack.nutritrackbackend.dto.response.food.FoodResponse;
import com.nutritrack.nutritrackbackend.dto.response.recipe.RecipeResponse;
import com.nutritrack.nutritrackbackend.enums.MealType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyLogEntryResponse {

    private Long id;
    private FoodResponse food;
    private RecipeResponse recipe;
    private Double quantity;
    private MealType mealType;

    // customNutrition
    private Double calories;
    private Double protein;
    private Double fat;
    private Double carbs;
    private Double sugar;
    private Double salt;
    private Double saturatedFat;
}
