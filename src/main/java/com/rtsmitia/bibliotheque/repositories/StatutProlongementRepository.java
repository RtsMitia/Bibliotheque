package com.rtsmitia.bibliotheque.repositories;

import com.rtsmitia.bibliotheque.models.StatutProlongement;
import com.rtsmitia.bibliotheque.models.Prolongement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatutProlongementRepository extends JpaRepository<StatutProlongement, Long> {

    // Find all statuts for a specific prolongement
    List<StatutProlongement> findByProlongement(Prolongement prolongement);

    // Find statuts by prolongement ID
    List<StatutProlongement> findByProlongementId(Long prolongementId);

    // Find statuts by status type
    List<StatutProlongement> findByStatut(String statut);

    // Find the most recent statut for a prolongement
    @Query("SELECT s FROM StatutProlongement s WHERE s.prolongement = :prolongement ORDER BY s.dateChangement DESC LIMIT 1")
    StatutProlongement findMostRecentByProlongement(@Param("prolongement") Prolongement prolongement);

    // Find statuts within a date range
    @Query("SELECT s FROM StatutProlongement s WHERE s.dateChangement BETWEEN :startDate AND :endDate")
    List<StatutProlongement> findStatutsInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Find statuts by status and date range
    @Query("SELECT s FROM StatutProlongement s WHERE s.statut = :statut AND s.dateChangement BETWEEN :startDate AND :endDate")
    List<StatutProlongement> findByStatutAndDateRange(@Param("statut") String statut, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Find all prolongements with a specific status ordered by date
    @Query("SELECT s FROM StatutProlongement s WHERE s.statut = :statut ORDER BY s.dateChangement DESC")
    List<StatutProlongement> findByStatutOrderByDateDesc(@Param("statut") String statut);
}
