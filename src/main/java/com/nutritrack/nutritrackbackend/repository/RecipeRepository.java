package com.nutritrack.nutritrackbackend.repository;

import com.nutritrack.nutritrackbackend.entity.Recipe;
import com.nutritrack.nutritrackbackend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Page<Recipe> findByIsPublicTrueOrCreatedBy(User createdBy, Pageable pageable);
}
