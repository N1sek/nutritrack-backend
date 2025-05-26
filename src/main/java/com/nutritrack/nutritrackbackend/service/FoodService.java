package com.nutritrack.nutritrackbackend.service;

import com.nutritrack.nutritrackbackend.dto.request.food.FoodRequest;
import com.nutritrack.nutritrackbackend.dto.response.food.FoodResponse;
import com.nutritrack.nutritrackbackend.entity.Food;
import com.nutritrack.nutritrackbackend.entity.User;

import java.util.List;
import java.util.Optional;

public interface FoodService {
    Food create(FoodRequest request, User creator);
    List<FoodResponse> getAll();
    Optional<Food> findById(Long id);
    List<FoodResponse> searchByName(String query);
    FoodResponse importExternalFood(FoodRequest request);
    List<FoodResponse> searchLocalFoods(String query);
    List<FoodResponse> searchExternalFoods(String query, int page, int size);
    List<FoodResponse> searchAllFoods(String query);


}

