package com.powerstats.controller;

import com.powerstats.dto.ChartRequest;
import com.powerstats.service.ChartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/chart")
@CrossOrigin(origins = "*")
public class ChartController {

    private final ChartService chartService;

    public ChartController(ChartService chartService) {
        this.chartService = chartService;
    }

    /**
     * GET /api/chart?type=LINE&timeBase=DAILY&startDate=2024-01-01&endDate=2024-12-31
     * Returns chart as PNG image
     */
    @GetMapping(produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getChart(
            @RequestParam(defaultValue = "LINE") ChartRequest.ChartType type,
            @RequestParam(defaultValue = "DAILY") ChartRequest.TimeBase timeBase,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        ChartRequest chartRequest = ChartRequest.builder()
                .chartType(type)
                .timeBase(timeBase)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        byte[] chartImage = chartService.generateChart(chartRequest);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=chart.png")
                .contentType(MediaType.IMAGE_PNG)
                .body(chartImage);
    }
}
