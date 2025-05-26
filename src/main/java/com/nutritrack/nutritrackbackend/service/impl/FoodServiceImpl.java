package com.nutritrack.nutritrackbackend.service.impl;

import com.nutritrack.nutritrackbackend.dto.request.food.FoodRequest;
import com.nutritrack.nutritrackbackend.dto.response.food.FoodResponse;
import com.nutritrack.nutritrackbackend.entity.Allergen;
import com.nutritrack.nutritrackbackend.entity.Food;
import com.nutritrack.nutritrackbackend.entity.User;
import com.nutritrack.nutritrackbackend.mapper.FoodMapper;
import com.nutritrack.nutritrackbackend.repository.FoodRepository;
import com.nutritrack.nutritrackbackend.service.AllergenService;
import com.nutritrack.nutritrackbackend.service.FoodService;
import com.nutritrack.nutritrackbackend.service.OpenFoodFactsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {

    private final FoodRepository foodRepository;
    private final AllergenService allergenService;
    private final FoodMapper foodMapper;
    private final OpenFoodFactsService openFoodFactsService;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public Food create(FoodRequest request, User creator) {
        Set<Allergen> allergens = request.getAllergenIds() == null ? new HashSet<>() :
                new HashSet<>(allergenService.findAllByIds(new ArrayList<>(request.getAllergenIds())));

        Food food = foodMapper.toEntity(request, allergens, creator);
        return foodRepository.save(food);
    }

    @Override
    public List<FoodResponse> getAll() {
        return foodRepository.findAll()
                .stream()
                .map(foodMapper::toResponse)
                .toList();
    }

    @Override
    public Optional<Food> findById(Long id) {
        return foodRepository.findById(id);
    }

    @Override
    public List<FoodResponse> searchByName(String query) {
        return foodRepository.findByNameContainingIgnoreCase(query)
                .stream()
                .map(foodMapper::toResponse)
                .toList();
    }

    @Override
    public FoodResponse importExternalFood(FoodRequest request) {
        Optional<Food> existing = foodRepository
                .findByNameIgnoreCaseAndImageUrl(request.getName(), request.getImageUrl());

        if (existing.isPresent()) {
            return foodMapper.toResponse(existing.get());
        }

        Set<Allergen> allergens = request.getAllergenIds() == null ? new HashSet<>() :
                new HashSet<>(allergenService.findAllByIds(new ArrayList<>(request.getAllergenIds())));

        Food newFood = Food.builder()
                .name(request.getName())
                .imageUrl(request.getImageUrl())
                .calories(round(request.getCalories()))
                .protein(round(request.getProtein()))
                .fat(round(request.getFat()))
                .carbs(round(request.getCarbs()))
                .sugar(round(request.getSugar()))
                .salt(round(request.getSalt()))
                .saturatedFat(round(request.getSaturatedFat()))
                .imported(true)
                .allergens(allergens)
                .build();

        foodRepository.save(newFood);
        return foodMapper.toResponse(newFood);
    }

    @Override
    public List<FoodResponse> searchLocalFoods(String query) {
        return foodRepository.findByNameContainingIgnoreCase(query).stream()
                .map(foodMapper::toResponse)
                .toList();
    }

    @Override
    public List<FoodResponse> searchExternalFoods(String query, int page, int size) {
        return openFoodFactsService.searchExternalFoods(query, page, size);
    }


    @Override
    public List<FoodResponse> searchAllFoods(String query) {
        List<FoodResponse> localFoods = searchLocalFoods(query);

        int externalPage = 1;
        int externalSize = 10;

        CompletableFuture<List<FoodResponse>> externalFuture = CompletableFuture.supplyAsync(() ->
                openFoodFactsService.searchExternalFoods(query, externalPage, externalSize), executor
        );

        try {
            List<FoodResponse> externalFoods = externalFuture.get(2, TimeUnit.SECONDS);

            // Eliminar duplicados
            Set<String> localKeys = localFoods.stream()
                    .map(f -> f.getName().toLowerCase() + "|" + f.getImageUrl())
                    .collect(Collectors.toSet());

            List<FoodResponse> filteredExternals = externalFoods.stream()
                    .filter(f -> !localKeys.contains(f.getName().toLowerCase() + "|" + f.getImageUrl()))
                    .toList();

            List<FoodResponse> combined = new ArrayList<>(localFoods);
            combined.addAll(filteredExternals);
            return combined;

        } catch (Exception e) {
            System.err.println("Error al obtener alimentos externos: " + e.getMessage());
            return localFoods;
        }
    }


    private Double round(Double value) {
        if (value == null) return null;
        return Math.round(value * 100.0) / 100.0;
    }

}
