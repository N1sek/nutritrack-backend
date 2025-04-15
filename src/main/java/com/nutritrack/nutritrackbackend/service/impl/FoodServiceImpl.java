package com.nutritrack.nutritrackbackend.service.impl;

import com.nutritrack.nutritrackbackend.dto.request.food.FoodRequest;
import com.nutritrack.nutritrackbackend.dto.response.food.FoodResponse;
import com.nutritrack.nutritrackbackend.entity.Allergen;
import com.nutritrack.nutritrackbackend.entity.Food;
import com.nutritrack.nutritrackbackend.entity.User;
import com.nutritrack.nutritrackbackend.mapper.FoodMapper;
import com.nutritrack.nutritrackbackend.mapper.OpenFoodFactsMapper;
import com.nutritrack.nutritrackbackend.repository.FoodRepository;
import com.nutritrack.nutritrackbackend.service.AllergenService;
import com.nutritrack.nutritrackbackend.service.FoodService;
import com.nutritrack.nutritrackbackend.service.OpenFoodFactsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {

    private final FoodRepository foodRepository;
    private final AllergenService allergenService;
    private final FoodMapper foodMapper;
    private final OpenFoodFactsService openFoodFactsService;


    @Override
    public Food create(FoodRequest request, User creator) {
        Set<Allergen> allergens = new HashSet<>();
        if (request.getAllergenIds() != null && !request.getAllergenIds().isEmpty()) {
            allergens = new HashSet<>(allergenService.findAllByIds(new ArrayList<>(request.getAllergenIds())));
        }


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

        Set<Allergen> allergens = new HashSet<>();
        if (request.getAllergenIds() != null && !request.getAllergenIds().isEmpty()) {
            List<Long> ids = new ArrayList<>(request.getAllergenIds());
            allergens = new HashSet<>(allergenService.findAllByIds(ids));
        }


        Food newFood = Food.builder()
                .name(request.getName())
                .imageUrl(request.getImageUrl())
                .calories(request.getCalories())
                .protein(request.getProtein())
                .fat(request.getFat())
                .carbs(request.getCarbs())
                .sugar(request.getSugar())
                .salt(request.getSalt())
                .saturatedFat(request.getSaturatedFat())
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
    public List<FoodResponse> searchExternalFoods(String query) {
        List<FoodResponse> externalFoods = openFoodFactsService.searchExternalFoods(query);

        return externalFoods.stream()
                .limit(5)
                .toList();
    }


    @Override
    public List<FoodResponse> searchAllFoods(String query) {
        List<FoodResponse> localFoods = searchLocalFoods(query);
        List<FoodResponse> externalFoods = searchExternalFoods(query);

        // Evitar duplicados por name + imageUrl
        Set<String> localKeys = localFoods.stream()
                .map(f -> f.getName().toLowerCase() + "|" + f.getImageUrl())
                .collect(Collectors.toSet());

        List<FoodResponse> filteredExternals = externalFoods.stream()
                .filter(f -> !localKeys.contains(f.getName().toLowerCase() + "|" + f.getImageUrl()))
                .toList();

        List<FoodResponse> combined = new ArrayList<>(localFoods);
        combined.addAll(filteredExternals);

        return combined;
    }





}

