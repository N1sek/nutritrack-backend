package com.nutritrack.nutritrackbackend.mapper;

import com.nutritrack.nutritrackbackend.dto.request.dailylog.CustomNutritionDTO;
import com.nutritrack.nutritrackbackend.dto.response.dailylog.DailyLogEntryResponse;
import com.nutritrack.nutritrackbackend.dto.response.dailylog.DailyLogMealBreakdownResponse;
import com.nutritrack.nutritrackbackend.dto.response.dailylog.DailyLogResponse;
import com.nutritrack.nutritrackbackend.dto.response.food.FoodResponse;
import com.nutritrack.nutritrackbackend.dto.response.recipe.RecipeResponse;
import com.nutritrack.nutritrackbackend.entity.CustomNutrition;
import com.nutritrack.nutritrackbackend.entity.DailyLog;
import com.nutritrack.nutritrackbackend.entity.DailyLogEntry;
import com.nutritrack.nutritrackbackend.enums.MealType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DailyLogMapper {

    private final FoodMapper   foodMapper;
    private final RecipeMapper recipeMapper;

    public DailyLogResponse toResponse(DailyLog log) {
        List<DailyLogEntryResponse> entryResponses = log.getEntries().stream()
                .map(this::mapEntry)
                .toList();

        double totalCalories      = 0.0;
        double totalProtein       = 0.0;
        double totalFat           = 0.0;
        double totalCarbs         = 0.0;
        double totalSugar         = 0.0;
        double totalSalt          = 0.0;
        double totalSaturatedFat  = 0.0;

        Map<MealType, DailyLogMealBreakdownResponse> breakdownMap = new HashMap<>();

        for (DailyLogEntry entry : log.getEntries()) {
            CustomNutrition cn = entry.getCustomNutrition();
            double factor = entry.getQuantity() / 100.0;
            MealType mt = entry.getMealType();

            breakdownMap.putIfAbsent(mt, DailyLogMealBreakdownResponse.builder()
                    .mealType(mt)
                    .build());
            DailyLogMealBreakdownResponse br = breakdownMap.get(mt);

            if (cn != null) {
                if (cn.getCalories() != null) {
                    double v = cn.getCalories();
                    totalCalories     += v;
                    br.setTotalCalories(br.getTotalCalories() + v);
                }
                if (cn.getProtein() != null) {
                    double v = cn.getProtein();
                    totalProtein      += v;
                    br.setTotalProtein(br.getTotalProtein() + v);
                }
                if (cn.getFat() != null) {
                    double v = cn.getFat();
                    totalFat          += v;
                    br.setTotalFat(br.getTotalFat() + v);
                }
                if (cn.getCarbs() != null) {
                    double v = cn.getCarbs();
                    totalCarbs        += v;
                    br.setTotalCarbs(br.getTotalCarbs() + v);
                }
                if (cn.getSugar() != null) {
                    double v = cn.getSugar();
                    totalSugar        += v;
                    br.setTotalSugar(br.getTotalSugar() + v);
                }
                if (cn.getSalt() != null) {
                    double v = cn.getSalt();
                    totalSalt         += v;
                    br.setTotalSalt(br.getTotalSalt() + v);
                }
                if (cn.getSaturatedFat() != null) {
                    double v = cn.getSaturatedFat();
                    totalSaturatedFat += v;
                    br.setTotalSaturatedFat(br.getTotalSaturatedFat() + v);
                }
                continue;
            }

            // Si no hay customNutrition, map por food/recipe original
            if (entry.getFood() != null) {
                FoodResponse f = foodMapper.toResponse(entry.getFood());
                if (f.getCalories() != null) {
                    double v = f.getCalories() * factor;
                    totalCalories     += v;
                    br.setTotalCalories(br.getTotalCalories() + v);
                }
                if (f.getProtein() != null) {
                    double v = f.getProtein() * factor;
                    totalProtein      += v;
                    br.setTotalProtein(br.getTotalProtein() + v);
                }
                if (f.getFat() != null) {
                    double v = f.getFat() * factor;
                    totalFat          += v;
                    br.setTotalFat(br.getTotalFat() + v);
                }
                if (f.getCarbs() != null) {
                    double v = f.getCarbs() * factor;
                    totalCarbs        += v;
                    br.setTotalCarbs(br.getTotalCarbs() + v);
                }
                if (f.getSugar() != null) {
                    double v = f.getSugar() * factor;
                    totalSugar        += v;
                    br.setTotalSugar(br.getTotalSugar() + v);
                }
                if (f.getSalt() != null) {
                    double v = f.getSalt() * factor;
                    totalSalt         += v;
                    br.setTotalSalt(br.getTotalSalt() + v);
                }
                if (f.getSaturatedFat() != null) {
                    double v = f.getSaturatedFat() * factor;
                    totalSaturatedFat += v;
                    br.setTotalSaturatedFat(br.getTotalSaturatedFat() + v);
                }
            }

            if (entry.getRecipe() != null) {
                RecipeResponse r = recipeMapper.toResponse(
                        entry.getRecipe(),
                        log.getUser().getNickname()
                );
                if (r.getCalories() != null) {
                    double v = r.getCalories() * factor;
                    totalCalories     += v;
                    br.setTotalCalories(br.getTotalCalories() + v);
                }
                if (r.getProtein() != null) {
                    double v = r.getProtein() * factor;
                    totalProtein      += v;
                    br.setTotalProtein(br.getTotalProtein() + v);
                }
                if (r.getFat() != null) {
                    double v = r.getFat() * factor;
                    totalFat          += v;
                    br.setTotalFat(br.getTotalFat() + v);
                }
                if (r.getCarbs() != null) {
                    double v = r.getCarbs() * factor;
                    totalCarbs        += v;
                    br.setTotalCarbs(br.getTotalCarbs() + v);
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
        FoodResponse   foodResponse   = null;
        RecipeResponse recipeResponse = null;

        if (entry.getFood() != null) {
            foodResponse = foodMapper.toResponse(entry.getFood());
        }
        if (entry.getRecipe() != null) {
            recipeResponse = recipeMapper.toResponse(
                    entry.getRecipe(),
                    entry.getDailyLog().getUser().getNickname()
            );
        }

        var builder = DailyLogEntryResponse.builder()
                .id(entry.getId())
                .food(foodResponse)
                .recipe(recipeResponse)
                .quantity(entry.getQuantity())
                .mealType(entry.getMealType());

        CustomNutrition cn = entry.getCustomNutrition();
        if (cn != null) {
            builder
                    .calories(     cn.getCalories()       != null ? round(cn.getCalories())       : null)
                    .protein(      cn.getProtein()        != null ? round(cn.getProtein())        : null)
                    .fat(          cn.getFat()            != null ? round(cn.getFat())            : null)
                    .carbs(        cn.getCarbs()          != null ? round(cn.getCarbs())          : null)
                    .sugar(        cn.getSugar()          != null ? round(cn.getSugar())          : null)
                    .salt(         cn.getSalt()           != null ? round(cn.getSalt())           : null)
                    .saturatedFat( cn.getSaturatedFat()   != null ? round(cn.getSaturatedFat())   : null);
        }

        return builder.build();
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
