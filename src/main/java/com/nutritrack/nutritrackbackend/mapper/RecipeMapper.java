package com.nutritrack.nutritrackbackend.mapper;

import com.nutritrack.nutritrackbackend.dto.response.allergen.AllergenResponse;
import com.nutritrack.nutritrackbackend.dto.response.recipe.RecipeIngredientResponse;
import com.nutritrack.nutritrackbackend.dto.response.recipe.RecipeResponse;
import com.nutritrack.nutritrackbackend.entity.Allergen;
import com.nutritrack.nutritrackbackend.entity.Food;
import com.nutritrack.nutritrackbackend.entity.Recipe;
import com.nutritrack.nutritrackbackend.entity.RecipeIngredient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class RecipeMapper {

    private final AllergenMapper allergenMapper;
    private final FoodMapper foodMapper;

    public RecipeResponse toResponse(Recipe recipe, String currentUserNickname) {
        double totalCalories = 0.0;
        double totalProtein = 0.0;
        double totalFat = 0.0;
        double totalCarbs = 0.0;

        List<RecipeIngredientResponse> ingredientResponses = new ArrayList<>();
        Set<AllergenResponse> allergenResponses = new HashSet<>();

        for (RecipeIngredient ri : recipe.getIngredients()) {
            Food food = ri.getFood();
            double quantity = ri.getQuantity();

            if (food.getCalories() != null) totalCalories += (food.getCalories() * quantity / 100.0);
            if (food.getProtein() != null) totalProtein += (food.getProtein() * quantity / 100.0);
            if (food.getFat() != null) totalFat += (food.getFat() * quantity / 100.0);
            if (food.getCarbs() != null) totalCarbs += (food.getCarbs() * quantity / 100.0);

            ingredientResponses.add(
                    RecipeIngredientResponse.builder()
                            .food(foodMapper.toResponse(food))
                            .quantity(quantity)
                            .build()
            );

            if (food.getAllergens() != null) {
                for (Allergen allergen : food.getAllergens()) {
                    allergenResponses.add(allergenMapper.toResponse(allergen));
                }
            }
        }

        int favoritesCount = recipe.getFavoritedBy() != null ? recipe.getFavoritedBy().size() : 0;
        boolean isFavorited = false;

        if (currentUserNickname != null && recipe.getFavoritedBy() != null) {
            isFavorited = recipe.getFavoritedBy().stream()
                    .anyMatch(u -> u.getNickname().equalsIgnoreCase(currentUserNickname));
        }

        return RecipeResponse.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .description(recipe.getDescription())
                .instructions(recipe.getInstructions())
                .imageUrl(recipe.getImageUrl())
                .mealType(recipe.getMealType())
                .isPublic(recipe.isPublic())
                .ingredients(ingredientResponses)
                .calories(round(totalCalories))
                .protein(round(totalProtein))
                .fat(round(totalFat))
                .carbs(round(totalCarbs))
                .allergens(allergenResponses)
                .createdBy(recipe.getCreatedBy() != null ? recipe.getCreatedBy().getNickname() : null)
                .favoritesCount(favoritesCount)
                .isFavorited(isFavorited)
                .createdAt(recipe.getCreatedAt())
                .build();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

}
