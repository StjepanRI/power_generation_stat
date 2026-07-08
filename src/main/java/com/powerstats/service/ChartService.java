package com.powerstats.service;

import com.powerstats.dto.ChartRequest;
import com.powerstats.dto.PowerGenerationDTO;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
public class ChartService {

    private final DataExtractionService dataExtractionService;

    public ChartService(DataExtractionService dataExtractionService) {
        this.dataExtractionService = dataExtractionService;
    }

    /**
     * Generates chart based on request parameters
     */
    public byte[] generateChart(ChartRequest chartRequest) {
        try {
            List<PowerGenerationDTO> data = dataExtractionService
                    .getDataByDateRange(chartRequest.getStartDate(), chartRequest.getEndDate());

            if (data.isEmpty()) {
                log.warn("No data found for chart generation");
                return new byte[0];
            }

            // Aggregate data by time base
            Map<String, Double> aggregatedData = aggregateDataByTimeBase(data, chartRequest.getTimeBase());

            // Generate chart
            XYChart chart = createChart(chartRequest, aggregatedData);
            return chartToBytes(chart);

        } catch (Exception e) {
            log.error("Error generating chart", e);
            return new byte[0];
        }
    }

    /**
     * Aggregates data based on time base (daily, weekly, monthly, yearly)
     */
    private Map<String, Double> aggregateDataByTimeBase(List<PowerGenerationDTO> data,
                                                         ChartRequest.TimeBase timeBase) {
        Map<String, Double> aggregated = new LinkedHashMap<>();

        for (PowerGenerationDTO item : data) {
            String key = getTimeBaseKey(item.getDataDate(), timeBase);
            Double value = item.getGeneratedPower();

            aggregated.put(key, aggregated.getOrDefault(key, 0.0) + (value != null ? value : 0.0));
        }

        return aggregated;
    }

    /**
     * Gets a key for grouping by time base
     */
    private String getTimeBaseKey(LocalDate date, ChartRequest.TimeBase timeBase) {
        return switch (timeBase) {
            case DAILY -> date.toString();
            case WEEKLY -> {
                LocalDate weekStart = date.minusDays(date.getDayOfWeek().getValue() - 1);
                yield "Week of " + weekStart;
            }
            case MONTHLY -> date.getYear() + "-" + String.format("%02d", date.getMonthValue());
            case YEARLY -> String.valueOf(date.getYear());
        };
    }

    /**
     * Creates XYChart based on chart type
     */
    private XYChart createChart(ChartRequest chartRequest, Map<String, Double> data) {
        List<Double> xData = new ArrayList<>();
        List<Double> yData = new ArrayList<>();

        int index = 0;
        for (Double value : data.values()) {
            xData.add((double) index);
            yData.add(value);
            index++;
        }

        String chartTitle = "Power Generation - " + chartRequest.getTimeBase();
        XYChartBuilder builder = new XYChartBuilder()
                .title(chartTitle)
                .xAxisTitle("Time Period")
                .yAxisTitle("Power Generated (kWh)")
                .width(1024)
                .height(600);

        XYChart chart = builder.build();
        chart.addSeries("Power Generation", xData, yData);

        return chart;
    }

    /**
     * Converts chart to byte array
     */
    private byte[] chartToBytes(XYChart chart) throws IOException {
        File tempFile = File.createTempFile("chart", ".png");
        tempFile.deleteOnExit();

        org.knowm.xchart.BitmapEncoder.saveBitmap(chart, tempFile.getAbsolutePath(), org.knowm.xchart.BitmapFormat.PNG);

        byte[] fileBytes = java.nio.file.Files.readAllBytes(tempFile.toPath());
        tempFile.delete();

        return fileBytes;
    }
}
