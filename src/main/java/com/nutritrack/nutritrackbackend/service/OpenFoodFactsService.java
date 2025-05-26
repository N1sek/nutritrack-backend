package com.nutritrack.nutritrackbackend.service;

import com.nutritrack.nutritrackbackend.dto.response.food.FoodResponse;

import java.util.List;

public interface OpenFoodFactsService {
    List<FoodResponse> searchExternalFoods(String query, int page, int size);

}

