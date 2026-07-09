package com.powerstats.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PowerGenerationDTO {

    private Long id;

    @JsonProperty("data_date")
    private LocalDate dataDate;

    @JsonProperty("generated_power")
    private Double generatedPower;

    @JsonProperty("consumed_power")
    private Double consumedPower;

    @JsonProperty("peak_power")
    private Double peakPower;

    @JsonProperty("average_power")
    private Double averagePower;

    private Double efficiency;
}
