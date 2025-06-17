package com.nutritrack.nutritrackbackend.repository;

import com.nutritrack.nutritrackbackend.entity.DailyLog;
import com.nutritrack.nutritrackbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {
    Optional<DailyLog> findByUserAndDate(User user, LocalDate date);
    List<DailyLog> findAllByUserAndDateBetween(User user, LocalDate start, LocalDate end);

    void deleteAllByUser(User user);
}
