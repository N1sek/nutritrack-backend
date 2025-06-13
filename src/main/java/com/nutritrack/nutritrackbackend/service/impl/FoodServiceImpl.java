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
import com.nutritrack.nutritrackbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final UserService userService;

    @Override
    public Food create(FoodRequest request, User creator) {
        Set<Allergen> allergens = request.getAllergenIds() == null ? new HashSet<>() :
                new HashSet<>(allergenService.findAllByIds(new ArrayList<>(request.getAllergenIds())));

        Food food = foodMapper.toEntity(request, allergens, creator);
        return foodRepository.save(food);
    }

    @Override
    public List<FoodResponse> getFoodsByUser(User user) {
        return foodRepository.findAllByCreatedBy(user).stream()
                .map(foodMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteByIdAndUser(Long id, User user) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Alimento no encontrado"));
        if (!food.getCreatedBy().equals(user)) {
            throw new SecurityException("No puedes eliminar este alimento");
        }
        foodRepository.delete(food);
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

        User admin = userService.findByEmail("admin@nutritrack.com")
                .orElseThrow(() -> new RuntimeException("No se encontró el usuario admin del sistema"));

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
                .createdBy(admin)
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

    @Override
    public List<FoodResponse> getAllFoods() {
        return foodRepository.findAll().stream()
                .map(foodMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FoodResponse getFoodById(Long id) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Alimento no encontrado: " + id));
        return foodMapper.toResponse(food);
    }

    @Override
    @Transactional
    public FoodResponse createFood(FoodRequest request) {
        User creator = getCurrentUser();
        Set<Allergen> allergens = loadAllergens(request.getAllergenIds());
        Food toSave = foodMapper.toEntity(request, allergens, creator);
        Food saved = foodRepository.save(toSave);
        return foodMapper.toResponse(saved);
    }


    @Override
    @Transactional
    public FoodResponse updateFood(Long id, FoodRequest request) {
        Food existing = foodRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Alimento no encontrado: " + id));
        Set<Allergen> allergens = loadAllergens(request.getAllergenIds());
        foodMapper.updateEntityFromRequest(request, allergens, existing);
        Food updated = foodRepository.save(existing);
        return foodMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteFood(Long id) {
        Food existing = foodRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Alimento no encontrado: " + id));
        foodRepository.delete(existing);
    }

    private Set<Allergen> loadAllergens(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptySet();
        }
        List<Long> idList = new ArrayList<>(ids);
        return new HashSet<>(allergenService.findAllByIds(idList));
    }


    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.getByEmail(auth.getName());
    }


    private Double round(Double value) {
        if (value == null) return null;
        return Math.round(value * 100.0) / 100.0;
    }

}
