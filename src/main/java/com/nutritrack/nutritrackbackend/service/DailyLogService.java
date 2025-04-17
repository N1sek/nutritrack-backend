package com.nutritrack.nutritrackbackend.service;

import com.nutritrack.nutritrackbackend.dto.request.dailylog.DailyLogEntryRequest;
import com.nutritrack.nutritrackbackend.dto.request.dailylog.DailyLogRequest;
import com.nutritrack.nutritrackbackend.dto.response.dailylog.DailyLogResponse;
import com.nutritrack.nutritrackbackend.entity.User;

import java.time.LocalDate;

public interface DailyLogService {

    DailyLogResponse getLogByDate(User user, LocalDate date);

    DailyLogResponse addOrUpdateEntries(User user, DailyLogRequest request);


    void deleteEntry(User user, Long entryId);
}
