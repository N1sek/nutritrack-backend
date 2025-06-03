package com.nutritrack.nutritrackbackend.service;

import com.nutritrack.nutritrackbackend.dto.request.dailylog.DailyLogEntryRequest;
import com.nutritrack.nutritrackbackend.dto.request.dailylog.DailyLogRequest;
import com.nutritrack.nutritrackbackend.dto.response.dailylog.DailyLogResponse;
import com.nutritrack.nutritrackbackend.entity.DailyLog;
import com.nutritrack.nutritrackbackend.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyLogService {

    DailyLogResponse getLogByDate(User user, LocalDate date);
    DailyLogResponse addOrUpdateEntries(User user, DailyLogRequest request);
    List<DailyLogResponse> getLogsInRange(User user, LocalDate start, LocalDate end);
    List<DailyLogResponse> getExistingLogsInRange(User user, LocalDate start, LocalDate end);
    void deleteEntry(User user, Long entryId);
}
