package com.nutritrack.nutritrackbackend.service;

import com.nutritrack.nutritrackbackend.entity.Allergen;

import java.util.List;

public interface AllergenService {
    List<Allergen> findAllByIds(List<Long> ids);
}
