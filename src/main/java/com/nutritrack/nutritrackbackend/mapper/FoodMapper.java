package com.nutritrack.nutritrackbackend.mapper;

import com.nutritrack.nutritrackbackend.dto.request.food.FoodRequest;
import com.nutritrack.nutritrackbackend.dto.response.allergen.AllergenResponse;
import com.nutritrack.nutritrackbackend.dto.response.food.FoodResponse;
import com.nutritrack.nutritrackbackend.entity.Allergen;
import com.nutritrack.nutritrackbackend.entity.Food;
import com.nutritrack.nutritrackbackend.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FoodMapper {

    private final AllergenMapper allergenMapper;

    public FoodMapper(AllergenMapper allergenMapper) {
        this.allergenMapper = allergenMapper;
    }

    public Food toEntity(FoodRequest request, Set<Allergen> allergens, User creator) {
        return Food.builder()
                .name(request.getName())
                .imageUrl(request.getImageUrl())
                .calories(request.getCalories())
                .protein(request.getProtein())
                .fat(request.getFat())
                .carbs(request.getCarbs())
                .createdBy(creator)
                .imported(false)
                .createdAt(LocalDateTime.now())
                .allergens(allergens)
                .build();
    }

    public FoodResponse toResponse(Food food) {
        Set<AllergenResponse> allergenDTOs = food.getAllergens()
                .stream()
                .map(allergenMapper::toResponse)
                .collect(Collectors.toSet());

        return FoodResponse.builder()
                .id(food.getId())
                .name(food.getName())
                .imageUrl(food.getImageUrl())
                .calories(round(food.getCalories()))
                .protein(round(food.getProtein()))
                .fat(round(food.getFat()))
                .carbs(round(food.getCarbs()))
                .sugar(round(food.getSugar()))
                .salt(round(food.getSalt()))
                .saturatedFat(round(food.getSaturatedFat()))
                .allergens(allergenDTOs)
                .createdBy(food.getCreatedBy() != null ? food.getCreatedBy().getNickname() : null)
                .imported(food.isImported())
                .build();

    }

    private Double round(Double value) {
        if (value == null) return null;
        return Math.round(value * 100.0) / 100.0;
    }
}

