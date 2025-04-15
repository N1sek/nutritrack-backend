package com.nutritrack.nutritrackbackend.dto.response.recipe;

import com.nutritrack.nutritrackbackend.dto.response.food.FoodResponse;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeIngredientResponse {
    private FoodResponse food;
    private Double quantity;
}
