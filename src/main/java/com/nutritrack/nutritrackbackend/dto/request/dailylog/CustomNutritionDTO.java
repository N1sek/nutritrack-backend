package com.nutritrack.nutritrackbackend.dto.request.dailylog;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomNutritionDTO {
    private Double calories;
    private Double protein;
    private Double fat;
    private Double carbs;
    private Double sugar;
    private Double salt;
    private Double saturatedFat;
}

