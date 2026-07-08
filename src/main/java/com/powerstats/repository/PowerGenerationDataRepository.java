package com.powerstats.repository;

import com.powerstats.entity.PowerGenerationData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PowerGenerationDataRepository extends JpaRepository<PowerGenerationData, Long> {

    List<PowerGenerationData> findByDataDate(LocalDate date);

    List<PowerGenerationData> findByDataDateBetweenOrderByDataDate(LocalDate startDate, LocalDate endDate);

    @Query("SELECT p FROM PowerGenerationData p WHERE p.dataDate = :date")
    Optional<PowerGenerationData> findByExactDate(@Param("date") LocalDate date);

    @Query("SELECT AVG(p.generatedPower) FROM PowerGenerationData p WHERE p.dataDate BETWEEN :startDate AND :endDate")
    Double getAveragePowerGeneration(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT MAX(p.generatedPower) FROM PowerGenerationData p WHERE p.dataDate BETWEEN :startDate AND :endDate")
    Double getMaxPowerGeneration(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
