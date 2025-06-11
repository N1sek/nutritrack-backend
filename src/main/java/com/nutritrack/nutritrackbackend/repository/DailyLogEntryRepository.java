package com.nutritrack.nutritrackbackend.repository;

import com.nutritrack.nutritrackbackend.entity.DailyLogEntry;
import com.nutritrack.nutritrackbackend.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DailyLogEntryRepository extends JpaRepository<DailyLogEntry, Long> {
    void deleteAllByRecipe(Recipe recipe);
    List<DailyLogEntry> findAllByRecipe(Recipe recipe);
}
