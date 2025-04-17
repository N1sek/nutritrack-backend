package com.nutritrack.nutritrackbackend.repository;

import com.nutritrack.nutritrackbackend.entity.DailyLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyLogEntryRepository extends JpaRepository<DailyLogEntry, Long> {
}
