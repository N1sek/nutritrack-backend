package com.nutritrack.nutritrackbackend.dto.response.recipe;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeIngredientResponse {
    private Long foodId;
    private String foodName;
    private Double quantityInGrams;
}

