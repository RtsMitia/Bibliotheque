package com.rtsmitia.bibliotheque.repositories;

import com.rtsmitia.bibliotheque.models.JourFerie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JourFerieRepository extends JpaRepository<JourFerie, Long> {

    /**
     * Find holidays that contain a specific date
     */
    @Query("SELECT j FROM JourFerie j WHERE :date >= j.dateDebut AND :date <= COALESCE(j.dateFin, j.dateDebut)")
    List<JourFerie> findHolidaysContainingDate(@Param("date") LocalDate date);

    /**
     * Find holidays within a date range
     */
    @Query("SELECT j FROM JourFerie j WHERE j.dateDebut <= :endDate AND COALESCE(j.dateFin, j.dateDebut) >= :startDate")
    List<JourFerie> findHolidaysInRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find holidays starting from a specific date
     */
    @Query("SELECT j FROM JourFerie j WHERE j.dateDebut >= :fromDate ORDER BY j.dateDebut ASC")
    List<JourFerie> findHolidaysFromDate(@Param("fromDate") LocalDate fromDate);

    /**
     * Check if a specific date is a holiday
     */
    @Query("SELECT COUNT(j) > 0 FROM JourFerie j WHERE :date >= j.dateDebut AND :date <= COALESCE(j.dateFin, j.dateDebut)")
    boolean isDateHoliday(@Param("date") LocalDate date);

    /**
     * Find overlapping holidays for validation
     */
    @Query("SELECT j FROM JourFerie j WHERE j.dateDebut <= :endDate AND COALESCE(j.dateFin, j.dateDebut) >= :startDate AND j.id != :excludeId")
    List<JourFerie> findOverlappingHolidays(@Param("startDate") LocalDate startDate, 
                                          @Param("endDate") LocalDate endDate, 
                                          @Param("excludeId") Long excludeId);
}
