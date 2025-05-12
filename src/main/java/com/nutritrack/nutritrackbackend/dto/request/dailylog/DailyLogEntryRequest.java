package com.nutritrack.nutritrackbackend.dto.request.dailylog;

import com.nutritrack.nutritrackbackend.enums.MealType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyLogEntryRequest {

    private Long foodId;

    private Long recipeId;

    @NotNull
    private Double quantity;

    @NotNull
    private MealType mealType;

    private CustomNutritionDTO customNutrition;
}
