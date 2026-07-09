package com.powerstats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChartRequest {

    private ChartType chartType; // LINE, BAR, AREA, SCATTER, PIE
    private TimeBase timeBase;   // DAILY, WEEKLY, MONTHLY, YEARLY
    private LocalDate startDate;
    private LocalDate endDate;
    private String dataField;   // Which field to display (generatedPower, etc.)

    public enum ChartType {
        LINE,
        BAR,
        AREA,
        SCATTER,
        PIE,
        CANDLESTICK
    }

    public enum TimeBase {
        DAILY,
        WEEKLY,
        MONTHLY,
        YEARLY
    }
}
