package com.nutritrack.nutritrackbackend.dto.response.dailylog;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyLogResponse {

    private Long id;
    private LocalDate date;
    private List<DailyLogEntryResponse> entries;

    private Double totalCalories;
    private Double totalProtein;
    private Double totalFat;
    private Double totalCarbs;
    private Double totalSugar;
    private Double totalSalt;
    private Double totalSaturatedFat;

    private List<DailyLogMealBreakdownResponse> breakdownByMealType;
}
