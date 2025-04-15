package com.nutritrack.nutritrackbackend.service;

import com.nutritrack.nutritrackbackend.dto.request.recipe.RecipeRequest;
import com.nutritrack.nutritrackbackend.dto.response.recipe.RecipeResponse;
import com.nutritrack.nutritrackbackend.entity.User;
import com.nutritrack.nutritrackbackend.enums.MealType;

import java.util.List;
import java.util.Optional;

public interface RecipeService {
    RecipeResponse create(RecipeRequest request, User creator);
    List<RecipeResponse> getAll(String currentUserNickname);
    Optional<RecipeResponse> getById(Long id, String currentUserNickname);
    List<RecipeResponse> getFavorites(String currentUserNickname);
    void toggleFavorite(Long recipeId, User user);
    List<RecipeResponse> searchRecipes(
            String nickname,
            String name,
            boolean favoritesOnly,
            List<Long> excludedAllergens,
            MealType mealType,
            String sort,
            Double minCalories,
            Double maxCalories
    );


}
