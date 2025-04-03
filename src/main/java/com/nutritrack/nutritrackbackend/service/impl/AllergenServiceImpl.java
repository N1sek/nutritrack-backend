package com.nutritrack.nutritrackbackend.service.impl;

import com.nutritrack.nutritrackbackend.entity.Allergen;
import com.nutritrack.nutritrackbackend.repository.AllergenRepository;
import com.nutritrack.nutritrackbackend.service.AllergenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AllergenServiceImpl implements AllergenService {

    private final AllergenRepository allergenRepository;

    @Override
    public List<Allergen> findAllByIds(List<Long> ids) {
        return allergenRepository.findByIdIn(ids);
    }
}
