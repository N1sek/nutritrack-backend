package com.nutritrack.nutritrackbackend.service.impl;

import com.nutritrack.nutritrackbackend.dto.request.dailylog.CustomNutritionDTO;
import com.nutritrack.nutritrackbackend.dto.request.dailylog.DailyLogEntryRequest;
import com.nutritrack.nutritrackbackend.dto.request.dailylog.DailyLogRequest;
import com.nutritrack.nutritrackbackend.dto.response.dailylog.DailyLogResponse;
import com.nutritrack.nutritrackbackend.entity.*;
import com.nutritrack.nutritrackbackend.mapper.DailyLogMapper;
import com.nutritrack.nutritrackbackend.repository.DailyLogEntryRepository;
import com.nutritrack.nutritrackbackend.repository.DailyLogRepository;
import com.nutritrack.nutritrackbackend.repository.FoodRepository;
import com.nutritrack.nutritrackbackend.repository.RecipeRepository;
import com.nutritrack.nutritrackbackend.service.DailyLogService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.StringWriter;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DailyLogServiceImpl implements DailyLogService {

    private final DailyLogRepository dailyLogRepository;
    private final DailyLogEntryRepository entryRepository;
    private final FoodRepository foodRepository;
    private final RecipeRepository recipeRepository;
    private final DailyLogMapper dailyLogMapper;

    @Override
    public List<DailyLogResponse> getExistingLogsInRange(User user, LocalDate start, LocalDate end) {
        return dailyLogRepository.findAllByUserAndDateBetween(user, start, end)
                .stream()
                .map(dailyLogMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DailyLogResponse getLogByDate(User user, LocalDate date) {
        DailyLog log = dailyLogRepository.findByUserAndDate(user, date)
                .orElseGet(() -> {
                    DailyLog newLog = new DailyLog();
                    newLog.setUser(user);
                    newLog.setDate(date);
                    newLog.setEntries(new HashSet<>());
                    return dailyLogRepository.save(newLog);
                });

        return dailyLogMapper.toResponse(log);
    }

    @Override
    @Transactional
    public DailyLogResponse addOrUpdateEntries(User user, DailyLogRequest request) {
        DailyLog log = dailyLogRepository.findByUserAndDate(user, request.getDate())
                .orElseGet(() -> {
                    DailyLog nuevo = new DailyLog();
                    nuevo.setUser(user);
                    nuevo.setDate(request.getDate());
                    nuevo.setEntries(new HashSet<>());
                    return dailyLogRepository.save(nuevo);
                });

        if (request.getFastingHours() != null) {
            log.setFastingHours(request.getFastingHours());
        }

        for (DailyLogEntryRequest dto : request.getEntries()) {
            DailyLogEntry.DailyLogEntryBuilder builder = DailyLogEntry.builder()
                    .dailyLog(log)
                    .quantity(dto.getQuantity())
                    .mealType(dto.getMealType());

            if (dto.getFoodId() != null) {
                Food f = foodRepository.findById(dto.getFoodId())
                        .orElseThrow(() -> new NoSuchElementException("Alimento no encontrado: " + dto.getFoodId()));
                builder.food(f);
            }
            if (dto.getRecipeId() != null) {
                Recipe r = recipeRepository.findById(dto.getRecipeId())
                        .orElseThrow(() -> new NoSuchElementException("Receta no encontrada: " + dto.getRecipeId()));
                builder.recipe(r);
            }
            if (dto.getCustomNutrition() != null) {
                CustomNutritionDTO cn = dto.getCustomNutrition();
                CustomNutrition custom = CustomNutrition.builder()
                        .calories(cn.getCalories())
                        .protein(cn.getProtein())
                        .fat(cn.getFat())
                        .carbs(cn.getCarbs())
                        .sugar(cn.getSugar())
                        .salt(cn.getSalt())
                        .saturatedFat(cn.getSaturatedFat())
                        .build();
                builder.customNutrition(custom);
            }

            DailyLogEntry nuevaEntrada = builder.build();
            log.getEntries().add(nuevaEntrada);
        }

        DailyLog guardado = dailyLogRepository.save(log);

        return dailyLogMapper.toResponse(guardado);
    }



    @Override
    public void deleteEntry(User user, Long entryId) {
        DailyLogEntry entry = entryRepository.findById(entryId)
                .orElseThrow(() -> new NoSuchElementException("Entrada no encontrada"));

        if (!entry.getDailyLog().getUser().equals(user)) {
            throw new SecurityException("No puedes borrar esta entrada");
        }

        entryRepository.delete(entry);
    }

    @Override
    public List<DailyLogResponse> getLogsInRange(User user, LocalDate start, LocalDate end) {
        List<DailyLogResponse> result = new ArrayList<>();
        LocalDate cursor = start;
        while (!cursor.isAfter(end)) {
            DailyLogResponse dto = getLogByDate(user, cursor);
            result.add(dto);
            cursor = cursor.plusDays(1);
        }
        return result;
    }

    @Override
    public byte[] exportLogs(User user, LocalDate start, LocalDate end, String format) throws IOException {
        List<DailyLogResponse> logs = getExistingLogsInRange(user, start, end);

        return switch (format.toLowerCase()) {
            case "csv"   -> exportCsv(logs);
            case "excel" -> exportExcel(logs);
            case "pdf"   -> exportPdf(logs);
            default      -> throw new IllegalArgumentException("Formato no soportado: " + format);
        };
    }

    private byte[] exportCsv(List<DailyLogResponse> logs) throws IOException {
        try (StringWriter sw = new StringWriter();
             CSVWriter writer = new CSVWriter(sw)) {

            writer.writeNext(new String[]{"Fecha", "Calorías", "Proteínas", "Carbohidratos", "Grasas"});

            for (DailyLogResponse log : logs) {
                writer.writeNext(new String[]{
                        log.getDate().toString(),
                        log.getTotalCalories().toString(),
                        log.getTotalProtein().toString(),
                        log.getTotalCarbs().toString(),
                        log.getTotalFat().toString()
                });
            }
            writer.flush();
            return sw.toString().getBytes(StandardCharsets.UTF_8);
        }
    }

    private byte[] exportExcel(List<DailyLogResponse> logs) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            XSSFSheet sheet = workbook.createSheet("Informe");

            Row header = sheet.createRow(0);
            String[] cols = {"Fecha", "Calorías", "Proteínas", "Carbohidratos", "Grasas"};
            for (int i = 0; i < cols.length; i++) {
                header.createCell(i).setCellValue(cols[i]);
            }

            int rowIdx = 1;
            for (DailyLogResponse log : logs) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(log.getDate().toString());
                row.createCell(1).setCellValue(log.getTotalCalories());
                row.createCell(2).setCellValue(log.getTotalProtein());
                row.createCell(3).setCellValue(log.getTotalCarbs());
                row.createCell(4).setCellValue(log.getTotalFat());
            }
            workbook.write(bos);
            return bos.toByteArray();
        }
    }

    private byte[] exportPdf(List<DailyLogResponse> logs) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, bos);
            document.open();

            PdfPTable table = new PdfPTable(5);
            Stream.of("Fecha","Calorías","Proteínas","Carbohidratos","Grasas")
                    .forEach(table::addCell);

            for (DailyLogResponse log : logs) {
                table.addCell(log.getDate().toString());
                table.addCell(String.valueOf(log.getTotalCalories()));
                table.addCell(String.valueOf(log.getTotalProtein()));
                table.addCell(String.valueOf(log.getTotalCarbs()));
                table.addCell(String.valueOf(log.getTotalFat()));
            }

            document.add(table);
            document.close();
            return bos.toByteArray();
        }
    }
}
