package com.nutritrack.nutritrackbackend.controller;

import com.nutritrack.nutritrackbackend.dto.request.recipe.RecipeRequest;
import com.nutritrack.nutritrackbackend.dto.response.image.UploadImageResponse;
import com.nutritrack.nutritrackbackend.dto.response.recipe.RecipeResponse;
import com.nutritrack.nutritrackbackend.entity.User;
import com.nutritrack.nutritrackbackend.enums.MealType;
import com.nutritrack.nutritrackbackend.service.ImageStorageService;
import com.nutritrack.nutritrackbackend.service.RecipeService;
import com.nutritrack.nutritrackbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
    private final UserService userService;
    private final ImageStorageService imageStorageService;

    @PostMapping
    public ResponseEntity<RecipeResponse> createRecipe(
            @RequestBody RecipeRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.getByEmail(userDetails.getUsername());
        RecipeResponse response = recipeService.create(request, user);
        return ResponseEntity.ok(response);
    }

    // Obtener todas las recetas (publicas y propias)
    @GetMapping
    public ResponseEntity<Page<RecipeResponse>> getAllRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String nickname = userService.getByEmail(userDetails.getUsername()).getNickname();
        return ResponseEntity.ok(recipeService.getAll(page, size, nickname));
    }


    @GetMapping("/favorites")
    public ResponseEntity<List<RecipeResponse>> getFavoriteRecipes(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String nickname = userService.getByEmail(userDetails.getUsername()).getNickname();
        return ResponseEntity.ok(recipeService.getFavorites(nickname));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponse> getRecipeById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String nickname = userService.getByEmail(userDetails.getUsername()).getNickname();
        return recipeService.getById(id, nickname)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/favorite")
    public ResponseEntity<Void> toggleFavorite(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.getByEmail(userDetails.getUsername());
        recipeService.toggleFavorite(id, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<RecipeResponse>> getMyRecipes(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.getByEmail(userDetails.getUsername());
        List<RecipeResponse> mine = recipeService.getByUser(user);
        return ResponseEntity.ok(mine);
    }

    @GetMapping("/search")
    public ResponseEntity<List<RecipeResponse>> searchRecipes(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "false") boolean favoritesOnly,
            @RequestParam(required = false) List<Long> excludedAllergens,
            @RequestParam(required = false) MealType mealType,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Double minCalories,
            @RequestParam(required = false) Double maxCalories,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<RecipeResponse> results = recipeService.searchRecipes(
                userDetails.getUsername(),
                name,
                favoritesOnly,
                excludedAllergens,
                mealType,
                sort,
                minCalories,
                maxCalories
        );
        return ResponseEntity.ok(results);
    }

    @PostMapping("/upload")
    public ResponseEntity<UploadImageResponse> uploadImage(
            @RequestParam("image") MultipartFile image
    ) {
        String url = imageStorageService.store(image);
        return ResponseEntity.ok(new UploadImageResponse(url));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMyRecipe(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails ud
    ) {
        User user = userService.getByEmail(ud.getUsername());
        recipeService.deleteByIdAndUser(id, user);
        return ResponseEntity.noContent().build();
    }
}
