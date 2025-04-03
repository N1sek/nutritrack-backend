package com.nutritrack.nutritrackbackend.repository;

import com.nutritrack.nutritrackbackend.entity.Allergen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AllergenRepository extends JpaRepository<Allergen, Long> {
    List<Allergen> findByIdIn(List<Long> ids);
}
