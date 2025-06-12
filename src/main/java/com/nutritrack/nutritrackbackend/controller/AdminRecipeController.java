package com.nutritrack.nutritrackbackend.controller;

import com.nutritrack.nutritrackbackend.dto.request.recipe.RecipeRequest;
import com.nutritrack.nutritrackbackend.dto.response.recipe.RecipeResponse;
import com.nutritrack.nutritrackbackend.entity.User;
import com.nutritrack.nutritrackbackend.security.UserDetailsAdapter;
import com.nutritrack.nutritrackbackend.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/admin/recipes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminRecipeController {

    private final RecipeService recipeService;

    @GetMapping
    public ResponseEntity<Page<RecipeResponse>> listAll(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            Authentication authentication
    ) {
        String nickname = ((UserDetailsAdapter)authentication.getPrincipal())
                .getUser().getNickname();
        Page<RecipeResponse> recipes = recipeService.getAll(page, size, nickname);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponse> getOne(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String nickname = ((UserDetailsAdapter)authentication.getPrincipal()).getUser().getNickname();
        return recipeService.getById(id, nickname)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NoSuchElementException("Receta no encontrada: " + id));
    }

    @PostMapping
    public ResponseEntity<RecipeResponse> create(
            @RequestBody @Valid RecipeRequest request,
            Authentication authentication
    ) {
        User creator = ((UserDetailsAdapter)authentication.getPrincipal()).getUser();
        RecipeResponse created = recipeService.create(request, creator);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @PutMapping("/{id}")
    public ResponseEntity<RecipeResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid RecipeRequest request,
            Authentication authentication
    ) {
        User updater = ((UserDetailsAdapter)authentication.getPrincipal()).getUser();
        RecipeResponse updated = recipeService.update(id, request, updater);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        recipeService.deleteByIdAndUser(id, null);
        return ResponseEntity.noContent().build();
    }
}
