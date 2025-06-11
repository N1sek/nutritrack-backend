package com.nutritrack.nutritrackbackend.repository;

import com.nutritrack.nutritrackbackend.entity.Food;
import com.nutritrack.nutritrackbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {

    Optional<Food> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<Food> findByNameContainingIgnoreCase(String name);

    Optional<Food> findByNameIgnoreCaseAndImageUrl(String name, String imageUrl);

    List<Food> findAllByCreatedBy(User user);


}
