package com.nutritrack.nutritrackbackend.mapper;

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
        List<DailyLogEntryResponse> entries = log.getEntries().stream()
                .map(this::mapEntry)
                .toList();
        
        double totalC = 0, totalP = 0, totalF = 0, totalCarb = 0, totalSug = 0, totalSalt = 0, totalSat = 0;
        Map<MealType, DailyLogMealBreakdownResponse> breakdown = new HashMap<>();

        for (DailyLogEntry e : log.getEntries()) {
            DailyLogEntryResponse dto = mapEntry(e);
            MealType mt = e.getMealType();
            breakdown.putIfAbsent(mt, DailyLogMealBreakdownResponse.builder().mealType(mt).build());
            DailyLogMealBreakdownResponse b = breakdown.get(mt);

            totalC    += dto.getCalories();      b.setTotalCalories(b.getTotalCalories() + dto.getCalories());
            totalP    += dto.getProtein();       b.setTotalProtein(b.getTotalProtein() + dto.getProtein());
            totalF    += dto.getFat();           b.setTotalFat(b.getTotalFat() + dto.getFat());
            totalCarb += dto.getCarbs();         b.setTotalCarbs(b.getTotalCarbs() + dto.getCarbs());
            totalSug  += dto.getSugar();         b.setTotalSugar(b.getTotalSugar() + dto.getSugar());
            totalSalt += dto.getSalt();          b.setTotalSalt(b.getTotalSalt() + dto.getSalt());
            totalSat  += dto.getSaturatedFat();  b.setTotalSaturatedFat(b.getTotalSaturatedFat() + dto.getSaturatedFat());
        }

        return DailyLogResponse.builder()
                .id(log.getId())
                .date(log.getDate())
                .entries(entries)
                .totalCalories(round(totalC))
                .totalProtein(round(totalP))
                .totalFat(round(totalF))
                .totalCarbs(round(totalCarb))
                .totalSugar(round(totalSug))
                .totalSalt(round(totalSalt))
                .totalSaturatedFat(round(totalSat))
                .breakdownByMealType(
                        breakdown.values().stream()
                                .map(this::roundBreakdown)
                                .toList()
                )
                .fastingHours(log.getFastingHours())
                .build();
    }

    private DailyLogEntryResponse mapEntry(DailyLogEntry entry) {
        FoodResponse   fr = entry.getFood()   != null
                ? foodMapper.toResponse(entry.getFood())
                : null;
        RecipeResponse rr = entry.getRecipe() != null
                ? recipeMapper.toResponse(entry.getRecipe(), entry.getDailyLog().getUser().getNickname())
                : null;

        double factor = entry.getQuantity() / 100.0;

        double cals = 0, prot = 0, fats = 0, carbs = 0, sugar = 0, salt = 0, satFat = 0;
        CustomNutrition cn = entry.getCustomNutrition();

        if (cn != null) {
            if (cn.getCalories()      != null) cals   = cn.getCalories();
            if (cn.getProtein()       != null) prot   = cn.getProtein();
            if (cn.getFat()           != null) fats   = cn.getFat();
            if (cn.getCarbs()         != null) carbs  = cn.getCarbs();
            if (cn.getSugar()         != null) sugar  = cn.getSugar();
            if (cn.getSalt()          != null) salt   = cn.getSalt();
            if (cn.getSaturatedFat()  != null) satFat = cn.getSaturatedFat();
        } else {
            if (fr != null) {
                if (fr.getCalories()     != null) cals   = fr.getCalories()     * factor;
                if (fr.getProtein()      != null) prot   = fr.getProtein()      * factor;
                if (fr.getFat()          != null) fats   = fr.getFat()          * factor;
                if (fr.getCarbs()        != null) carbs  = fr.getCarbs()        * factor;
                if (fr.getSugar()        != null) sugar  = fr.getSugar()        * factor;
                if (fr.getSalt()         != null) salt   = fr.getSalt()         * factor;
                if (fr.getSaturatedFat() != null) satFat = fr.getSaturatedFat() * factor;
            }
            if (rr != null) {
                if (rr.getCalories()     != null) cals   += rr.getCalories()     * factor;
                if (rr.getProtein()      != null) prot   += rr.getProtein()      * factor;
                if (rr.getFat()          != null) fats   += rr.getFat()          * factor;
                if (rr.getCarbs()        != null) carbs  += rr.getCarbs()        * factor;
                if (rr.getSugar()        != null) sugar  += rr.getSugar()        * factor;
                if (rr.getSalt()         != null) salt   += rr.getSalt()         * factor;
                if (rr.getSaturatedFat() != null) satFat += rr.getSaturatedFat() * factor;
            }
        }

        return DailyLogEntryResponse.builder()
                .id(entry.getId())
                .food(fr)
                .recipe(rr)
                .quantity(entry.getQuantity())
                .mealType(entry.getMealType())
                .calories(Math.round(cals   * 100.0) / 100.0)
                .protein(Math.round(prot   * 100.0) / 100.0)
                .fat(Math.round(fats   * 100.0) / 100.0)
                .carbs(Math.round(carbs  * 100.0) / 100.0)
                .sugar(Math.round(sugar  * 100.0) / 100.0)
                .salt(Math.round(salt    * 100.0) / 100.0)
                .saturatedFat(Math.round(satFat * 100.0) / 100.0)
                .build();
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
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
