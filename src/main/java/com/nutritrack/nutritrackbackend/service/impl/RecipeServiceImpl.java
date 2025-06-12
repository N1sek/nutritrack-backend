package com.nutritrack.nutritrackbackend.service.impl;

import com.nutritrack.nutritrackbackend.dto.request.recipe.RecipeRequest;
import com.nutritrack.nutritrackbackend.dto.response.allergen.AllergenResponse;
import com.nutritrack.nutritrackbackend.dto.response.recipe.RecipeResponse;
import com.nutritrack.nutritrackbackend.entity.*;
import com.nutritrack.nutritrackbackend.enums.MealType;
import com.nutritrack.nutritrackbackend.mapper.RecipeMapper;
import com.nutritrack.nutritrackbackend.repository.DailyLogEntryRepository;
import com.nutritrack.nutritrackbackend.repository.FoodRepository;
import com.nutritrack.nutritrackbackend.repository.RecipeRepository;
import com.nutritrack.nutritrackbackend.service.RecipeService;
import com.nutritrack.nutritrackbackend.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final FoodRepository foodRepository;
    private final RecipeMapper recipeMapper;
    private final UserService userService;
    private final DailyLogEntryRepository entryRepository;

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
            if (food.getProtein() != null)  totalProtein  += food.getProtein()  * factor;
            if (food.getFat() != null)      totalFat      += food.getFat()      * factor;
            if (food.getCarbs() != null)    totalCarbs    += food.getCarbs()    * factor;

            RecipeIngredient ingredient = RecipeIngredient.builder()
                    .food(food)
                    .quantity(ing.getQuantity())
                    .build();
            ingredients.add(ingredient);
        }

        Recipe recipe = Recipe.builder()
                .name(request.getName())
                .description(request.getDescription())
                .instructions(request.getInstructions())
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

        for (RecipeIngredient ingredient : ingredients) {
            ingredient.setRecipe(recipe);
        }
        recipe.setIngredients(new HashSet<>(ingredients));

        Recipe saved = recipeRepository.save(recipe);
        return recipeMapper.toResponse(saved, creator.getNickname());
    }

    @Override
    public List<RecipeResponse> getByUser(User user) {
        return recipeRepository.findAllByCreatedBy(user).stream()
                .map(r -> recipeMapper.toResponse(r, user.getNickname()))
                .collect(Collectors.toList());
    }

    @Override
    public Page<RecipeResponse> getAll(int page, int size, String currentUserNickname) {
        User currentUser = userService.findByNickname(currentUserNickname)
                .orElseThrow(() -> new NoSuchElementException(
                        "No se ha encontrado el usuario con el nickname: " + currentUserNickname));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Recipe> todas = recipeRepository.findAll(pageable);

        List<Recipe> filtradas = todas.stream()
                .filter(r -> r.isPublic()
                        || r.getCreatedBy().getId().equals(currentUser.getId()))
                .toList();

        List<RecipeResponse> dtoList = filtradas.stream()
                .map(r -> recipeMapper.toResponse(r, currentUserNickname))
                .toList();

        return new PageImpl<>(
                dtoList,
                pageable,
                filtradas.size()
        );
    }

    @Override
    public Optional<RecipeResponse> getById(Long id, String currentUserNickname) {
        Optional<Recipe> optReceta = recipeRepository.findById(id);
        if (optReceta.isEmpty()) {
            return Optional.empty();
        }

        Recipe receta = optReceta.get();
        User currentUser = userService.findByNickname(currentUserNickname)
                .orElseThrow(() -> new NoSuchElementException(
                        "No se ha encontrado el usuario con el nickname: " + currentUserNickname));

        if (!receta.isPublic()
                && !receta.getCreatedBy().getId().equals(currentUser.getId())) {
            return Optional.empty();
        }

        return Optional.of(recipeMapper.toResponse(receta, currentUserNickname));
    }

    @Override
    @Transactional
    public void deleteByIdAndUser(Long id, User user) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Receta no encontrada"));
        if (!recipe.getCreatedBy().equals(user)) {
            throw new SecurityException("No puedes eliminar esta receta");
        }

        List<DailyLogEntry> entries = entryRepository.findAllByRecipe(recipe);

        for (DailyLogEntry e : entries) {
            double factor = e.getQuantity() / 100.0;
            CustomNutrition cn = Optional.ofNullable(e.getCustomNutrition())
                    .orElseGet(CustomNutrition::new);

            cn.setCalories(      recipe.getCalories()      * factor);
            cn.setProtein(       recipe.getProtein()       * factor);
            cn.setFat(           recipe.getFat()           * factor);
            cn.setCarbs(         recipe.getCarbs()         * factor);

            e.setCustomNutrition(cn);
            // Desvincular la receta para evitar la FK
            e.setRecipe(null);
        }

        entryRepository.saveAll(entries);
        
        recipeRepository.delete(recipe);
    }

    @Override
    @Transactional
    public RecipeResponse update(Long id, RecipeRequest request, User updater) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Receta no encontrada: " + id));
        if (
                !recipe.getCreatedBy().getId().equals(updater.getId())
                        && !updater.getRole().name().equals("ADMIN")
        ) {
            throw new SecurityException("No tienes permiso para modificar esta receta");
        }

        List<RecipeIngredient> newIngredients = new ArrayList<>();
        double totalCalories = 0, totalProtein = 0, totalFat = 0, totalCarbs = 0;
        for (var ingReq : request.getIngredients()) {
            if (ingReq.getFoodId() == null) {
                log.error("Uno de los ingredientes tiene foodId null: {}", ingReq);
                throw new IllegalArgumentException("Algún ingrediente no tiene foodId");
            }
            Food food = foodRepository.findById(ingReq.getFoodId())
                    .orElseThrow(() -> new NoSuchElementException("Food not found: " + ingReq.getFoodId()));
            double factor = ingReq.getQuantity() / 100.0;
            if (food.getCalories() != null) totalCalories += food.getCalories() * factor;
            if (food.getProtein()  != null) totalProtein  += food.getProtein()  * factor;
            if (food.getFat()      != null) totalFat      += food.getFat()      * factor;
            if (food.getCarbs()    != null) totalCarbs    += food.getCarbs()    * factor;

            RecipeIngredient ri = RecipeIngredient.builder()
                    .food(food)
                    .quantity(ingReq.getQuantity())
                    .recipe(recipe)
                    .build();
            newIngredients.add(ri);
        }

        recipe.setName(request.getName());
        recipe.setDescription(request.getDescription());
        recipe.setInstructions(request.getInstructions());
        recipe.setImageUrl(request.getImageUrl());
        recipe.setMealType(request.getMealType());
        recipe.setPublic(request.getIsPublic());

        recipe.getIngredients().clear();
        recipe.getIngredients().addAll(newIngredients);

        recipe.setCalories(round(totalCalories));
        recipe.setProtein(round(totalProtein));
        recipe.setFat(round(totalFat));
        recipe.setCarbs(round(totalCarbs));

        Recipe saved = recipeRepository.save(recipe);
        return recipeMapper.toResponse(saved, updater.getNickname());
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
                .filter(recipe -> recipe.isPublic()
                        || recipe.getCreatedBy().getNickname().equalsIgnoreCase(nickname))
                .map(recipe -> recipeMapper.toResponse(recipe, nickname))
                .filter(r -> name == null || r.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(r -> !favoritesOnly || r.isFavorited())
                .filter(r -> excludedAllergens == null || excludedAllergens.isEmpty()
                        || r.getAllergens().stream().map(AllergenResponse::getId)
                        .noneMatch(excludedAllergens::contains))
                .filter(r -> mealType == null || r.getMealType() == mealType)
                .filter(r -> minCalories == null || r.getCalories() != null && r.getCalories() >= minCalories)
                .filter(r -> maxCalories == null || r.getCalories() != null && r.getCalories() <= maxCalories)
                .sorted(getComparator(sort))
                .toList();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private Comparator<RecipeResponse> getComparator(String sort) {
        if (sort == null) return Comparator.comparing(RecipeResponse::getName);
        return switch (sort.toLowerCase()) {
            case "calories" -> Comparator.comparing(RecipeResponse::getCalories, Comparator.nullsLast(Double::compareTo));
            case "protein"  -> Comparator.comparing(RecipeResponse::getProtein, Comparator.nullsLast(Double::compareTo));
            case "carbs"    -> Comparator.comparing(RecipeResponse::getCarbs, Comparator.nullsLast(Double::compareTo));
            case "fat"      -> Comparator.comparing(RecipeResponse::getFat, Comparator.nullsLast(Double::compareTo));
            default         -> Comparator.comparing(RecipeResponse::getName);
        };
    }
}
