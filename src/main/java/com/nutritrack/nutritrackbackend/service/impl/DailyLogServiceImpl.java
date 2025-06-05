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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyLogServiceImpl implements DailyLogService {

    private final DailyLogRepository dailyLogRepository;
    private final DailyLogEntryRepository entryRepository;
    private final FoodRepository foodRepository;
    private final RecipeRepository recipeRepository;
    private final DailyLogMapper dailyLogMapper;

    @Override
    public List<DailyLogResponse> getExistingLogsInRange(User user, LocalDate start, LocalDate end) {
        return dailyLogRepository.findAllByUserAndDateBetween(user, start, end)
                .stream()
                .map(dailyLogMapper::toResponse)
                .collect(Collectors.toList());
    }

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
                    DailyLog nuevo = new DailyLog();
                    nuevo.setUser(user);
                    nuevo.setDate(request.getDate());
                    nuevo.setEntries(new HashSet<>());
                    return dailyLogRepository.save(nuevo);
                });

        if (request.getFastingHours() != null) {
            log.setFastingHours(request.getFastingHours());
        }

        for (DailyLogEntryRequest dto : request.getEntries()) {
            DailyLogEntry.DailyLogEntryBuilder builder = DailyLogEntry.builder()
                    .dailyLog(log)
                    .quantity(dto.getQuantity())
                    .mealType(dto.getMealType());

            if (dto.getFoodId() != null) {
                Food f = foodRepository.findById(dto.getFoodId())
                        .orElseThrow(() -> new NoSuchElementException("Alimento no encontrado: " + dto.getFoodId()));
                builder.food(f);
            }
            if (dto.getRecipeId() != null) {
                Recipe r = recipeRepository.findById(dto.getRecipeId())
                        .orElseThrow(() -> new NoSuchElementException("Receta no encontrada: " + dto.getRecipeId()));
                builder.recipe(r);
            }
            if (dto.getCustomNutrition() != null) {
                CustomNutritionDTO cn = dto.getCustomNutrition();
                CustomNutrition custom = CustomNutrition.builder()
                        .calories(cn.getCalories())
                        .protein(cn.getProtein())
                        .fat(cn.getFat())
                        .carbs(cn.getCarbs())
                        .sugar(cn.getSugar())
                        .salt(cn.getSalt())
                        .saturatedFat(cn.getSaturatedFat())
                        .build();
                builder.customNutrition(custom);
            }

            DailyLogEntry nuevaEntrada = builder.build();
            log.getEntries().add(nuevaEntrada);
        }

        DailyLog guardado = dailyLogRepository.save(log);

        return dailyLogMapper.toResponse(guardado);
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

    @Override
    public List<DailyLogResponse> getLogsInRange(User user, LocalDate start, LocalDate end) {
        List<DailyLogResponse> result = new ArrayList<>();
        LocalDate cursor = start;
        while (!cursor.isAfter(end)) {
            DailyLogResponse dto = getLogByDate(user, cursor);
            result.add(dto);
            cursor = cursor.plusDays(1);
        }
        return result;
    }
}
