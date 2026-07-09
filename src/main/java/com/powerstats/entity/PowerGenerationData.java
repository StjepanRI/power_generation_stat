package com.powerstats.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "power_generation_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PowerGenerationData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dataDate;

    @Column(nullable = false)
    private Double generatedPower; // in kWh

    @Column(name = "consumed_power")
    private Double consumedPower; // in kWh (if available)

    @Column(name = "peak_power")
    private Double peakPower; // in kW

    @Column(name = "average_power")
    private Double averagePower; // in kW

    @Column(name = "efficiency")
    private Double efficiency; // percentage

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "email_source_id")
    private Long emailSourceId;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
