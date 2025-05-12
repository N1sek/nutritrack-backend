package com.nutritrack.nutritrackbackend.service.impl;

import com.nutritrack.nutritrackbackend.dto.request.dailylog.CustomNutritionDTO;
import com.nutritrack.nutritrackbackend.dto.request.dailylog.DailyLogEntryRequest;
import com.nutritrack.nutritrackbackend.dto.request.dailylog.DailyLogRequest;
import com.nutritrack.nutritrackbackend.dto.response.dailylog.DailyLogResponse;
import com.nutritrack.nutritrackbackend.entity.*;
import com.nutritrack.nutritrackbackend.mapper.DailyLogMapper;
import com.nutritrack.nutritrackbackend.repository.DailyLogEntryRepository;
import com.nutritrack.nutritrackbackend.repository.DailyLogRepository;
import com.nutritrack.nutritrackbackend.repository.FoodRepository;
import com.nutritrack.nutritrackbackend.repository.RecipeRepository;
import com.nutritrack.nutritrackbackend.service.DailyLogService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DailyLogServiceImpl implements DailyLogService {

    private final DailyLogRepository dailyLogRepository;
    private final DailyLogEntryRepository entryRepository;
    private final FoodRepository foodRepository;
    private final RecipeRepository recipeRepository;
    private final DailyLogMapper dailyLogMapper;

    @Override
    public DailyLogResponse getLogByDate(User user, LocalDate date) {
        DailyLog log = dailyLogRepository.findByUserAndDate(user, date)
                .orElseGet(() -> {
                    DailyLog newLog = new DailyLog();
                    newLog.setUser(user);
                    newLog.setDate(date);
                    newLog.setEntries(new HashSet<>());
                    return dailyLogRepository.save(newLog);
                });

        return dailyLogMapper.toResponse(log);
    }

    @Override
    @Transactional
    public DailyLogResponse addOrUpdateEntries(User user, DailyLogRequest request) {
        DailyLog log = dailyLogRepository.findByUserAndDate(user, request.getDate())
                .orElseGet(() -> {
                    DailyLog newLog = new DailyLog();
                    newLog.setUser(user);
                    newLog.setDate(request.getDate());
                    newLog.setEntries(new HashSet<>());
                    return dailyLogRepository.save(newLog);
                });

        for (DailyLogEntryRequest entryDto : request.getEntries()) {
            DailyLogEntry.DailyLogEntryBuilder builder = DailyLogEntry.builder()
                    .dailyLog(log)
                    .quantity(entryDto.getQuantity())
                    .mealType(entryDto.getMealType());

            if (entryDto.getFoodId() != null) {
                Food food = foodRepository.findById(entryDto.getFoodId())
                        .orElseThrow(() -> new NoSuchElementException("Alimento no encontrado: " + entryDto.getFoodId()));
                builder.food(food);
            }

            if (entryDto.getRecipeId() != null) {
                Recipe recipe = recipeRepository.findById(entryDto.getRecipeId())
                        .orElseThrow(() -> new NoSuchElementException("Receta no encontrada: " + entryDto.getRecipeId()));
                builder.recipe(recipe);
            }

            if (entryDto.getCustomNutrition() != null) {
                CustomNutritionDTO dto = entryDto.getCustomNutrition();
                CustomNutrition custom = CustomNutrition.builder()
                        .calories(dto.getCalories())
                        .protein(dto.getProtein())
                        .fat(dto.getFat())
                        .carbs(dto.getCarbs())
                        .sugar(dto.getSugar())
                        .salt(dto.getSalt())
                        .saturatedFat(dto.getSaturatedFat())
                        .build();
                builder.customNutrition(custom);
            }

            DailyLogEntry entry = builder.build();
            log.getEntries().add(entry);
        }

        dailyLogRepository.save(log);
        return dailyLogMapper.toResponse(log);
    }

    @Override
    public void deleteEntry(User user, Long entryId) {
        DailyLogEntry entry = entryRepository.findById(entryId)
                .orElseThrow(() -> new NoSuchElementException("Entrada no encontrada"));

        if (!entry.getDailyLog().getUser().equals(user)) {
            throw new SecurityException("No puedes borrar esta entrada");
        }

        entryRepository.delete(entry);
    }
}
