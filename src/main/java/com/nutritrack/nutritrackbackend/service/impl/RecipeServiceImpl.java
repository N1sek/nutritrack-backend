package com.nutritrack.nutritrackbackend.service.impl;

import com.nutritrack.nutritrackbackend.dto.request.recipe.RecipeRequest;
import com.nutritrack.nutritrackbackend.dto.response.allergen.AllergenResponse;
import com.nutritrack.nutritrackbackend.dto.response.recipe.RecipeResponse;
import com.nutritrack.nutritrackbackend.entity.*;
import com.nutritrack.nutritrackbackend.enums.MealType;
import com.nutritrack.nutritrackbackend.mapper.RecipeMapper;
import com.nutritrack.nutritrackbackend.repository.FoodRepository;
import com.nutritrack.nutritrackbackend.repository.RecipeRepository;
import com.nutritrack.nutritrackbackend.service.RecipeService;
import com.nutritrack.nutritrackbackend.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final FoodRepository foodRepository;
    private final RecipeMapper recipeMapper;
    private final UserService userService;

    @Override
    @Transactional
    public RecipeResponse create(RecipeRequest request, User creator) {
        List<RecipeIngredient> ingredients = new ArrayList<>();

        double totalCalories = 0;
        double totalProtein = 0;
        double totalFat = 0;
        double totalCarbs = 0;

        for (var ing : request.getIngredients()) {
            Food food = foodRepository.findById(ing.getFoodId())
                    .orElseThrow(() -> new NoSuchElementException("Food not found: " + ing.getFoodId()));

            double factor = ing.getQuantity() / 100.0;

            if (food.getCalories() != null) totalCalories += food.getCalories() * factor;
            if (food.getProtein() != null) totalProtein += food.getProtein() * factor;
            if (food.getFat() != null) totalFat += food.getFat() * factor;
            if (food.getCarbs() != null) totalCarbs += food.getCarbs() * factor;

            RecipeIngredient ingredient = RecipeIngredient.builder()
                    .food(food)
                    .quantity(ing.getQuantity())
                    .build();

            ingredients.add(ingredient);
        }


        Recipe recipe = Recipe.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .mealType(request.getMealType())
                .isPublic(request.getIsPublic())
                .ingredients(new HashSet<>())
                .createdBy(creator)
                .createdAt(LocalDateTime.now())
                .calories(round(totalCalories))
                .protein(round(totalProtein))
                .fat(round(totalFat))
                .carbs(round(totalCarbs))
                .build();


        // Relacionar ingredientes con la receta
        for (RecipeIngredient ingredient : ingredients) {
            ingredient.setRecipe(recipe);
        }

        recipe.setIngredients(new HashSet<>(ingredients));
        Recipe saved = recipeRepository.save(recipe);

        return recipeMapper.toResponse(saved, creator.getNickname());
    }

    @Override
    public List<RecipeResponse> getAll(String currentUserNickname) {
        return recipeRepository.findAll().stream()
                .map(r -> recipeMapper.toResponse(r, currentUserNickname))
                .toList();
    }

    @Override
    public Optional<RecipeResponse> getById(Long id, String currentUserNickname) {
        return recipeRepository.findById(id)
                .map(r -> recipeMapper.toResponse(r, currentUserNickname));
    }

    @Override
    public List<RecipeResponse> getFavorites(String currentUserNickname) {
        return recipeRepository.findAll().stream()
                .filter(r -> r.getFavoritedBy().stream()
                        .anyMatch(u -> u.getNickname().equalsIgnoreCase(currentUserNickname)))
                .map(r -> recipeMapper.toResponse(r, currentUserNickname))
                .toList();
    }

    @Override
    @Transactional
    public void toggleFavorite(Long recipeId, User user) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NoSuchElementException("Receta no encontrada"));

        if (recipe.getFavoritedBy().contains(user)) {
            recipe.getFavoritedBy().remove(user);
        } else {
            recipe.getFavoritedBy().add(user);
        }

        recipeRepository.save(recipe);
    }

    @Override
    public List<RecipeResponse> searchRecipes(
            String email,
            String name,
            boolean favoritesOnly,
            List<Long> excludedAllergens,
            MealType mealType,
            String sort,
            Double minCalories,
            Double maxCalories
    ) {
        String nickname = userService.getByEmail(email).getNickname();

        return recipeRepository.findAll().stream()
                .filter(recipe -> recipe.isPublic() ||
                        recipe.getCreatedBy().getNickname().equalsIgnoreCase(nickname))
                .map(recipe -> recipeMapper.toResponse(recipe, nickname))
                .filter(r -> name == null || r.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(r -> !favoritesOnly || r.isFavorited())
                .filter(r -> excludedAllergens == null || excludedAllergens.isEmpty()
                        || r.getAllergens().stream().map(AllergenResponse::getId).noneMatch(excludedAllergens::contains))
                .filter(r -> mealType == null || r.getMealType() == mealType)
                .filter(r -> minCalories == null || r.getCalories() != null && r.getCalories() >= minCalories)
                .filter(r -> maxCalories == null || r.getCalories() != null && r.getCalories() <= maxCalories)
                .sorted(getComparator(sort))
                .toList();
    }


    private double calculateCalories(Recipe r) {
        return r.getIngredients().stream()
                .mapToDouble(i -> {
                    Double foodCalories = i.getFood().getCalories();
                    return foodCalories != null ? foodCalories * i.getQuantity() : 0.0;
                })
                .sum();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }


    private Comparator<RecipeResponse> getComparator(String sort) {
        if (sort == null) return Comparator.comparing(RecipeResponse::getName);

        return switch (sort.toLowerCase()) {
            case "calories" -> Comparator.comparing(RecipeResponse::getCalories, Comparator.nullsLast(Double::compareTo));
            case "protein" -> Comparator.comparing(RecipeResponse::getProtein, Comparator.nullsLast(Double::compareTo));
            case "carbs"   -> Comparator.comparing(RecipeResponse::getCarbs, Comparator.nullsLast(Double::compareTo));
            case "fat"     -> Comparator.comparing(RecipeResponse::getFat, Comparator.nullsLast(Double::compareTo));
            default        -> Comparator.comparing(RecipeResponse::getName);
        };
    }



}
