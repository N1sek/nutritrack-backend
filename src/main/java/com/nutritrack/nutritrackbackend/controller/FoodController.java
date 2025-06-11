package com.nutritrack.nutritrackbackend.controller;

import com.nutritrack.nutritrackbackend.dto.request.food.FoodRequest;
import com.nutritrack.nutritrackbackend.dto.response.food.FoodResponse;
import com.nutritrack.nutritrackbackend.entity.Food;
import com.nutritrack.nutritrackbackend.entity.User;
import com.nutritrack.nutritrackbackend.mapper.FoodMapper;
import com.nutritrack.nutritrackbackend.security.UserDetailsAdapter;
import com.nutritrack.nutritrackbackend.service.AllergenService;
import com.nutritrack.nutritrackbackend.service.FoodService;
import com.nutritrack.nutritrackbackend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/foods")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;
    private final FoodMapper foodMapper;
    private final AllergenService allergenService;
    private final UserService userService;

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

    @GetMapping("/my")
    public ResponseEntity<List<FoodResponse>> getMyFoods(
            @AuthenticationPrincipal UserDetails ud
    ) {
        User user = userService.getByEmail(ud.getUsername());
        List<FoodResponse> list = foodService.getFoodsByUser(user);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/search/local")
    public ResponseEntity<List<FoodResponse>> searchLocalFoods(@RequestParam String query) {
        return ResponseEntity.ok(foodService.searchLocalFoods(query));
    }

    @GetMapping("/search/external")
    public ResponseEntity<List<FoodResponse>> searchExternalFoods(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(foodService.searchExternalFoods(query, page, size));
    }


    @GetMapping("/search")
    public ResponseEntity<List<FoodResponse>> searchAllFoods(@RequestParam String query) {
        return ResponseEntity.ok(foodService.searchAllFoods(query));
    }

    @PostMapping("/import")
    public ResponseEntity<FoodResponse> importExternalFood(@RequestBody @Valid FoodRequest request) {
        FoodResponse saved = foodService.importExternalFood(request);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMyFood(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails ud
    ) {
        User user = userService.getByEmail(ud.getUsername());
        foodService.deleteByIdAndUser(id, user);
        return ResponseEntity.noContent().build();
    }
}
