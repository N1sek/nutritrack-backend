package com.nutritrack.nutritrackbackend.repository;

import com.nutritrack.nutritrackbackend.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

}
