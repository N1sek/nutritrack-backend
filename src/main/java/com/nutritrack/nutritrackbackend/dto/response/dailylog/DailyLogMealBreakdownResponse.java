package com.nutritrack.nutritrackbackend.dto.response.dailylog;

import com.nutritrack.nutritrackbackend.enums.MealType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyLogMealBreakdownResponse {
    private MealType mealType;
    private double totalCalories;
    private double totalProtein;
    private double totalFat;
    private double totalCarbs;
    private double totalSugar;
    private double totalSalt;
    private double totalSaturatedFat;
}
