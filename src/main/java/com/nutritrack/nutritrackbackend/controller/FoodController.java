package com.nutritrack.nutritrackbackend.controller;

import com.nutritrack.nutritrackbackend.dto.request.food.FoodRequest;
import com.nutritrack.nutritrackbackend.dto.response.food.FoodResponse;
import com.nutritrack.nutritrackbackend.entity.Food;
import com.nutritrack.nutritrackbackend.entity.User;
import com.nutritrack.nutritrackbackend.mapper.FoodMapper;
import com.nutritrack.nutritrackbackend.security.UserDetailsAdapter;
import com.nutritrack.nutritrackbackend.service.AllergenService;
import com.nutritrack.nutritrackbackend.service.FoodService;
import com.nutritrack.nutritrackbackend.service.OpenFoodFactsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/foods")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;
    private final FoodMapper foodMapper;
    private final AllergenService allergenService;
    private final OpenFoodFactsService openFoodFactsService;

    @PostMapping
    public ResponseEntity<FoodResponse> createFood(
            @RequestBody @Valid FoodRequest request,
            Authentication authentication
    ) {
        User user = ((UserDetailsAdapter) authentication.getPrincipal()).getUser();
        Food food = foodService.create(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(foodMapper.toResponse(food));
    }

    @GetMapping
    public ResponseEntity<List<FoodResponse>> getAllFoods() {
        return ResponseEntity.ok(foodService.getAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<FoodResponse>> searchFoods(@RequestParam String query) {
        List<FoodResponse> localMatches = foodService.searchByName(query);

        if (!localMatches.isEmpty()) {
            return ResponseEntity.ok(localMatches);
        }

        // Aquí deberás implementar esto más adelante
        List<FoodResponse> externalMatches = openFoodFactsService.searchExternalFoods(query);
        return ResponseEntity.ok(externalMatches);
    }

}

