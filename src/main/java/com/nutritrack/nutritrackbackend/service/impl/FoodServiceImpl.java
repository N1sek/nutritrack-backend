package com.nutritrack.nutritrackbackend.service.impl;

import com.nutritrack.nutritrackbackend.dto.request.food.FoodRequest;
import com.nutritrack.nutritrackbackend.dto.response.food.FoodResponse;
import com.nutritrack.nutritrackbackend.entity.Allergen;
import com.nutritrack.nutritrackbackend.entity.Food;
import com.nutritrack.nutritrackbackend.entity.User;
import com.nutritrack.nutritrackbackend.mapper.FoodMapper;
import com.nutritrack.nutritrackbackend.repository.FoodRepository;
import com.nutritrack.nutritrackbackend.service.AllergenService;
import com.nutritrack.nutritrackbackend.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {

    private final FoodRepository foodRepository;
    private final AllergenService allergenService;
    private final FoodMapper foodMapper;

    @Override
    public Food create(FoodRequest request, User creator) {
        Set<Allergen> allergens = new HashSet<>();
        if (request.getAllergenIds() != null && !request.getAllergenIds().isEmpty()) {
            allergens = new HashSet<>(allergenService.findAllByIds(new ArrayList<>(request.getAllergenIds())));
        }


        Food food = foodMapper.toEntity(request, allergens, creator);
        return foodRepository.save(food);
    }

    @Override
    public List<FoodResponse> getAll() {
        return foodRepository.findAll()
                .stream()
                .map(foodMapper::toResponse)
                .toList();
    }

    @Override
    public Optional<Food> findById(Long id) {
        return foodRepository.findById(id);
    }

    @Override
    public List<FoodResponse> searchByName(String query) {
        return foodRepository.findByNameContainingIgnoreCase(query)
                .stream()
                .map(foodMapper::toResponse)
                .toList();
    }

}

