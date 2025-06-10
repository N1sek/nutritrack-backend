package com.nutritrack.nutritrackbackend.controller;

import com.nutritrack.nutritrackbackend.dto.request.dailylog.DailyLogRequest;
import com.nutritrack.nutritrackbackend.dto.response.dailylog.DailyLogResponse;
import com.nutritrack.nutritrackbackend.entity.User;
import com.nutritrack.nutritrackbackend.service.DailyLogService;
import com.nutritrack.nutritrackbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/daily-log")
@RequiredArgsConstructor
public class DailyLogController {

    private final DailyLogService dailyLogService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<DailyLogResponse> getLogByDate(
            @RequestParam("date") String dateStr,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.getByEmail(userDetails.getUsername());
        LocalDate date = LocalDate.parse(dateStr);
        return ResponseEntity.ok(dailyLogService.getLogByDate(user, date));
    }

    @PostMapping
    public ResponseEntity<DailyLogResponse> saveOrUpdateLog(
            @RequestBody DailyLogRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.getByEmail(userDetails.getUsername());
        return ResponseEntity.ok(dailyLogService.addOrUpdateEntries(user, request));
    }

    @DeleteMapping("/entry/{id}")
    public ResponseEntity<Void> deleteEntry(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.getByEmail(userDetails.getUsername());
        dailyLogService.deleteEntry(user, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/range")
    public ResponseEntity<List<DailyLogResponse>> getLogsInRange(
            @RequestParam("start") String startDateStr,
            @RequestParam("end")   String endDateStr,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.getByEmail(userDetails.getUsername());
        LocalDate start = LocalDate.parse(startDateStr);
        LocalDate end   = LocalDate.parse(endDateStr);
        List<DailyLogResponse> lista = dailyLogService.getLogsInRange(user, start, end);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/existing-range")
    public ResponseEntity<List<DailyLogResponse>> getExistingLogsInRange(
            @RequestParam("start") String startDateStr,
            @RequestParam("end")   String endDateStr,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.getByEmail(userDetails.getUsername());
        LocalDate start = LocalDate.parse(startDateStr);
        LocalDate end   = LocalDate.parse(endDateStr);
        List<DailyLogResponse> lista = dailyLogService.getExistingLogsInRange(user, start, end);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/export")
    public ResponseEntity<ByteArrayResource> exportLogs(
            @RequestParam("start") String startDateStr,
            @RequestParam("end")   String endDateStr,
            @RequestParam("format") String format,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws IOException {
        User user = userService.getByEmail(userDetails.getUsername());
        LocalDate start = LocalDate.parse(startDateStr);
        LocalDate end   = LocalDate.parse(endDateStr);

        byte[] data = dailyLogService.exportLogs(user, start, end, format);

        String ext, mediaType;
        switch (format.toLowerCase()) {
            case "csv"   -> { ext = "csv"; mediaType = "text/csv"; }
            case "excel" -> { ext = "xlsx"; mediaType =
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"; }
            case "pdf"   -> { ext = "pdf"; mediaType = "application/pdf"; }
            default      -> throw new IllegalArgumentException("Formato no soportado");
        }

        String filename = String.format("informe_%s_a_%s.%s", start, end, ext);
        var resource = new ByteArrayResource(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(mediaType))
                .contentLength(data.length)
                .body(resource);
    }
}
