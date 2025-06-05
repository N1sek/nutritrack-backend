package com.nutritrack.nutritrackbackend.mapper;

import com.nutritrack.nutritrackbackend.dto.response.dailylog.DailyLogEntryResponse;
import com.nutritrack.nutritrackbackend.dto.response.dailylog.DailyLogMealBreakdownResponse;
import com.nutritrack.nutritrackbackend.dto.response.dailylog.DailyLogResponse;
import com.nutritrack.nutritrackbackend.dto.response.food.FoodResponse;
import com.nutritrack.nutritrackbackend.dto.response.recipe.RecipeResponse;
import com.nutritrack.nutritrackbackend.entity.DailyLog;
import com.nutritrack.nutritrackbackend.entity.DailyLogEntry;
import com.nutritrack.nutritrackbackend.entity.Food;
import com.nutritrack.nutritrackbackend.entity.Recipe;
import com.nutritrack.nutritrackbackend.enums.MealType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class DailyLogMapper {

    private final FoodMapper foodMapper;
    private final RecipeMapper recipeMapper;

    public DailyLogResponse toResponse(DailyLog log) {
        List<DailyLogEntryResponse> entryResponses = log.getEntries().stream()
                .map(this::mapEntry)
                .toList();

        double totalCalories = 0.0;
        double totalProtein = 0.0;
        double totalFat = 0.0;
        double totalCarbs = 0.0;
        double totalSugar = 0.0;
        double totalSalt = 0.0;
        double totalSaturatedFat = 0.0;

        Map<MealType, DailyLogMealBreakdownResponse> breakdownMap = new HashMap<>();

        for (DailyLogEntry entry : log.getEntries()) {
            double factor = entry.getQuantity() / 100.0;
            MealType mealType = entry.getMealType();

            breakdownMap.putIfAbsent(mealType, DailyLogMealBreakdownResponse.builder()
                    .mealType(mealType)
                    .build());
            DailyLogMealBreakdownResponse breakdown = breakdownMap.get(mealType);

            if (entry.getFood() != null) {
                Food food = entry.getFood();
                if (food.getCalories() != null) {
                    double value = food.getCalories() * factor;
                    totalCalories += value;
                    breakdown.setTotalCalories(breakdown.getTotalCalories() + value);
                }
                if (food.getProtein() != null) {
                    double value = food.getProtein() * factor;
                    totalProtein += value;
                    breakdown.setTotalProtein(breakdown.getTotalProtein() + value);
                }
                if (food.getFat() != null) {
                    double value = food.getFat() * factor;
                    totalFat += value;
                    breakdown.setTotalFat(breakdown.getTotalFat() + value);
                }
                if (food.getCarbs() != null) {
                    double value = food.getCarbs() * factor;
                    totalCarbs += value;
                    breakdown.setTotalCarbs(breakdown.getTotalCarbs() + value);
                }
                if (food.getSugar() != null) {
                    double value = food.getSugar() * factor;
                    totalSugar += value;
                    breakdown.setTotalSugar(breakdown.getTotalSugar() + value);
                }
                if (food.getSalt() != null) {
                    double value = food.getSalt() * factor;
                    totalSalt += value;
                    breakdown.setTotalSalt(breakdown.getTotalSalt() + value);
                }
                if (food.getSaturatedFat() != null) {
                    double value = food.getSaturatedFat() * factor;
                    totalSaturatedFat += value;
                    breakdown.setTotalSaturatedFat(breakdown.getTotalSaturatedFat() + value);
                }
            }

            if (entry.getRecipe() != null) {
                RecipeResponse recipe = recipeMapper.toResponse(entry.getRecipe(), entry.getDailyLog().getUser().getNickname());

                if (recipe.getCalories() != null) {
                    double value = recipe.getCalories() * factor;
                    totalCalories += value;
                    breakdown.setTotalCalories(breakdown.getTotalCalories() + value);
                }
                if (recipe.getProtein() != null) {
                    double value = recipe.getProtein() * factor;
                    totalProtein += value;
                    breakdown.setTotalProtein(breakdown.getTotalProtein() + value);
                }
                if (recipe.getFat() != null) {
                    double value = recipe.getFat() * factor;
                    totalFat += value;
                    breakdown.setTotalFat(breakdown.getTotalFat() + value);
                }
                if (recipe.getCarbs() != null) {
                    double value = recipe.getCarbs() * factor;
                    totalCarbs += value;
                    breakdown.setTotalCarbs(breakdown.getTotalCarbs() + value);
                }
            }

        }


        return DailyLogResponse.builder()
                .id(log.getId())
                .date(log.getDate())
                .entries(entryResponses)
                .totalCalories(round(totalCalories))
                .totalProtein(round(totalProtein))
                .totalFat(round(totalFat))
                .totalCarbs(round(totalCarbs))
                .totalSugar(round(totalSugar))
                .totalSalt(round(totalSalt))
                .totalSaturatedFat(round(totalSaturatedFat))
                .breakdownByMealType(
                        breakdownMap.values().stream()
                                .map(this::roundBreakdown)
                                .toList()
                )
                .fastingHours(log.getFastingHours())
                .build();
    }

    private DailyLogEntryResponse mapEntry(DailyLogEntry entry) {
        FoodResponse foodResponse = null;
        RecipeResponse recipeResponse = null;

        if (entry.getFood() != null) {
            foodResponse = foodMapper.toResponse(entry.getFood());
        }

        if (entry.getRecipe() != null) {
            recipeResponse = recipeMapper.toResponse(entry.getRecipe(), entry.getDailyLog().getUser().getNickname());
        }

        return DailyLogEntryResponse.builder()
                .id(entry.getId())
                .food(foodResponse)
                .recipe(recipeResponse)
                .quantity(entry.getQuantity())
                .mealType(entry.getMealType())
                .build();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private DailyLogMealBreakdownResponse roundBreakdown(DailyLogMealBreakdownResponse b) {
        return DailyLogMealBreakdownResponse.builder()
                .mealType(b.getMealType())
                .totalCalories(round(b.getTotalCalories()))
                .totalProtein(round(b.getTotalProtein()))
                .totalFat(round(b.getTotalFat()))
                .totalCarbs(round(b.getTotalCarbs()))
                .totalSugar(round(b.getTotalSugar()))
                .totalSalt(round(b.getTotalSalt()))
                .totalSaturatedFat(round(b.getTotalSaturatedFat()))
                .build();
    }
}
