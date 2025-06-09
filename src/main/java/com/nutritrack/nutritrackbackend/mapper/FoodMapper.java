package com.nutritrack.nutritrackbackend.mapper;

import com.nutritrack.nutritrackbackend.dto.request.food.FoodRequest;
import com.nutritrack.nutritrackbackend.dto.response.allergen.AllergenResponse;
import com.nutritrack.nutritrackbackend.dto.response.food.FoodResponse;
import com.nutritrack.nutritrackbackend.entity.Allergen;
import com.nutritrack.nutritrackbackend.entity.Food;
import com.nutritrack.nutritrackbackend.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FoodMapper {

    private final AllergenMapper allergenMapper;

    public FoodMapper(AllergenMapper allergenMapper) {
        this.allergenMapper = allergenMapper;
    }

    /** Mappea un FoodRequest + alérgenos + creador → entidad nueva */
    public Food toEntity(FoodRequest req, Set<Allergen> allergens, User creator) {
        return Food.builder()
                .name(req.getName())
                .imageUrl(req.getImageUrl())
                .calories(req.getCalories())
                .protein(req.getProtein())
                .fat(req.getFat())
                .carbs(req.getCarbs())
                .sugar(req.getSugar())
                .salt(req.getSalt())
                .saturatedFat(req.getSaturatedFat())
                .allergens(allergens)
                .imported(false)
                .createdBy(creator)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /** Actualiza los campos editables de una entidad existente */
    public void updateEntityFromRequest(FoodRequest req, Set<Allergen> allergens, Food food) {
        food.setName(req.getName());
        food.setImageUrl(req.getImageUrl());
        food.setCalories(req.getCalories());
        food.setProtein(req.getProtein());
        food.setFat(req.getFat());
        food.setCarbs(req.getCarbs());
        food.setSugar(req.getSugar());
        food.setSalt(req.getSalt());
        food.setSaturatedFat(req.getSaturatedFat());
        food.setAllergens(allergens);
    }

    /** Entidad → DTO de respuesta */
    public FoodResponse toResponse(Food food) {
        var allergenDtos = food.getAllergens().stream()
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
                .allergens(allergenDtos)
                .imported(food.isImported())
                .createdBy(food.getCreatedBy() != null
                        ? food.getCreatedBy().getNickname()
                        : null)
                .build();
    }

    private Double round(Double v) {
        if (v == null) return null;
        return Math.round(v * 100.0) / 100.0;
    }
}
