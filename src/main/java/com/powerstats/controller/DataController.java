package com.powerstats.controller;

import com.powerstats.dto.PowerGenerationDTO;
import com.powerstats.service.DataExtractionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/data")
@CrossOrigin(origins = "*")
public class DataController {

    private final DataExtractionService dataExtractionService;

    public DataController(DataExtractionService dataExtractionService) {
        this.dataExtractionService = dataExtractionService;
    }

    /**
     * GET /api/data - Get all power generation data
     * GET /api/data?startDate=2024-01-01&endDate=2024-12-31 - Get filtered data
     */
    @GetMapping
    public ResponseEntity<List<PowerGenerationDTO>> getData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        List<PowerGenerationDTO> data = dataExtractionService.getDataByDateRange(startDate, endDate);
        return ResponseEntity.ok(data);
    }
}
