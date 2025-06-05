package com.nutritrack.nutritrackbackend.dto.request.dailylog;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyLogRequest {

    @NotNull
    private LocalDate date;

    @NotNull
    private List<DailyLogEntryRequest> entries;

    private Integer fastingHours;
}
