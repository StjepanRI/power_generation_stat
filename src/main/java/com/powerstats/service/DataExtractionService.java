package com.powerstats.service;

import com.powerstats.dto.PowerGenerationDTO;
import com.powerstats.email.EmailParser;
import com.powerstats.entity.PowerGenerationData;
import com.powerstats.repository.PowerGenerationDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DataExtractionService {

    private final PowerGenerationDataRepository powerGenerationDataRepository;
    private final EmailParser emailParser;

    public DataExtractionService(PowerGenerationDataRepository powerGenerationDataRepository,
                                 EmailParser emailParser) {
        this.powerGenerationDataRepository = powerGenerationDataRepository;
        this.emailParser = emailParser;
    }

    /**
     * Processes email content and extracts power generation data
     */
    public List<PowerGenerationData> extractFromEmailContent(String emailContent, Long emailSourceId) {
        List<PowerGenerationData> extractedData = new ArrayList<>();

        try {
            // Parse table from email content
            List<String[]> tableData = emailParser.parseEmailTable(emailContent);

            if (tableData.isEmpty()) {
                log.warn("No table data found in email content");
                return extractedData;
            }

            // Process table rows (skip header row if present)
            for (int i = 1; i < tableData.size(); i++) {
                String[] row = tableData.get(i);
                PowerGenerationData data = parseDataRow(row, emailSourceId);
                if (data != null) {
                    extractedData.add(data);
                }
            }

            log.info("Extracted {} power generation records from email", extractedData.size());

        } catch (Exception e) {
            log.error("Error extracting data from email content", e);
        }

        return extractedData;
    }

    /**
     * Parses a single data row from table
     * Expects: [Date, GeneratedPower, ConsumedPower, PeakPower, AveragePower, Efficiency]
     */
    private PowerGenerationData parseDataRow(String[] row, Long emailSourceId) {
        try {
            if (row.length < 2) {
                log.warn("Row has insufficient columns: {}", row.length);
                return null;
            }

            // Parse date (column 0)
            LocalDate dataDate = parseDate(row[0]);
            if (dataDate == null) {
                return null;
            }

            // Parse generated power (column 1)
            Double generatedPower = emailParser.extractNumericValue(row[1]);
            if (generatedPower == null) {
                return null;
            }

            PowerGenerationData data = PowerGenerationData.builder()
                    .dataDate(dataDate)
                    .generatedPower(generatedPower)
                    .emailSourceId(emailSourceId)
                    .build();

            // Parse optional fields if present
            if (row.length > 2) {
                data.setConsumedPower(emailParser.extractNumericValue(row[2]));
            }
            if (row.length > 3) {
                data.setPeakPower(emailParser.extractNumericValue(row[3]));
            }
            if (row.length > 4) {
                data.setAveragePower(emailParser.extractNumericValue(row[4]));
            }
            if (row.length > 5) {
                data.setEfficiency(emailParser.extractNumericValue(row[5]));
            }

            return data;

        } catch (Exception e) {
            log.error("Error parsing data row", e);
            return null;
        }
    }

    /**
     * Attempts to parse date in various formats
     */
    private LocalDate parseDate(String dateString) {
        String[] dateFormats = {
                "yyyy-MM-dd",
                "dd.MM.yyyy",
                "dd/MM/yyyy",
                "MM/dd/yyyy",
                "yyyy/MM/dd"
        };

        for (String format : dateFormats) {
            try {
                return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(format));
            } catch (Exception ignored) {
                // Try next format
            }
        }

        log.warn("Could not parse date: {}", dateString);
        return null;
    }

    /**
     * Saves extracted data to database
     */
    public List<PowerGenerationData> saveData(List<PowerGenerationData> dataList) {
        return powerGenerationDataRepository.saveAll(dataList);
    }

    /**
     * Retrieves data for a date range
     */
    public List<PowerGenerationDTO> getDataByDateRange(LocalDate startDate, LocalDate endDate) {
        List<PowerGenerationData> dataList = powerGenerationDataRepository
                .findByDataDateBetweenOrderByDataDate(startDate, endDate);

        return dataList.stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Converts entity to DTO
     */
    private PowerGenerationDTO toDTO(PowerGenerationData entity) {
        return PowerGenerationDTO.builder()
                .id(entity.getId())
                .dataDate(entity.getDataDate())
                .generatedPower(entity.getGeneratedPower())
                .consumedPower(entity.getConsumedPower())
                .peakPower(entity.getPeakPower())
                .averagePower(entity.getAveragePower())
                .efficiency(entity.getEfficiency())
                .build();
    }
}
