package com.rtsmitia.bibliotheque.repositories;

import com.rtsmitia.bibliotheque.models.Prolongement;
import com.rtsmitia.bibliotheque.models.Pret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProlongementRepository extends JpaRepository<Prolongement, Long> {

    // Find all prolongements for a specific pret
    List<Prolongement> findByPret(Pret pret);

    // Find prolongements by pret ID
    List<Prolongement> findByPretId(Long pretId);

    // Find prolongements within a date range
    @Query("SELECT p FROM Prolongement p WHERE p.dateProlongement BETWEEN :startDate AND :endDate")
    List<Prolongement> findProlongementsInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Find the most recent prolongement for a pret
    @Query("SELECT p FROM Prolongement p WHERE p.pret = :pret ORDER BY p.dateProlongement DESC LIMIT 1")
    Prolongement findMostRecentByPret(@Param("pret") Pret pret);

    // Count prolongements for a specific pret
    long countByPret(Pret pret);

    // Find prolongements with nouvelle date retour before a certain date
    @Query("SELECT p FROM Prolongement p WHERE p.nouvelleDateRetour < :date")
    List<Prolongement> findProlongementsWithRetourBefore(@Param("date") LocalDateTime date);
}
