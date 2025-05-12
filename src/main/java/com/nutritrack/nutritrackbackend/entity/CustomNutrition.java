package com.nutritrack.nutritrackbackend.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class CustomNutrition {
    private Double calories;
    private Double protein;
    private Double fat;
    private Double carbs;
    private Double sugar;
    private Double salt;
    private Double saturatedFat;
}
