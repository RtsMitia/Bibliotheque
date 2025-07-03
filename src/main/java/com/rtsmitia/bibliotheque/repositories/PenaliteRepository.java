package com.rtsmitia.bibliotheque.repositories;

import com.rtsmitia.bibliotheque.models.Adherent;
import com.rtsmitia.bibliotheque.models.Penalite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PenaliteRepository extends JpaRepository<Penalite, Long> {
    
    /**
     * Find all penalties for a specific adherent
     */
    List<Penalite> findByAdherent(Adherent adherent);
    
    /**
     * Find all active penalties for a specific adherent
     */
    @Query("SELECT p FROM Penalite p WHERE p.adherent = :adherent AND :date BETWEEN p.dateDebut AND p.dateFin")
    List<Penalite> findActivePenaltiesForAdherent(@Param("adherent") Adherent adherent, @Param("date") LocalDate date);
    
    /**
     * Find all active penalties for a specific adherent (current date)
     */
    @Query("SELECT p FROM Penalite p WHERE p.adherent = :adherent AND CURRENT_DATE BETWEEN p.dateDebut AND p.dateFin")
    List<Penalite> findActivePenaltiesForAdherent(@Param("adherent") Adherent adherent);
    
    /**
     * Check if an adherent has any active penalties
     */
    @Query("SELECT COUNT(p) > 0 FROM Penalite p WHERE p.adherent = :adherent AND CURRENT_DATE BETWEEN p.dateDebut AND p.dateFin")
    boolean hasActivePenalties(@Param("adherent") Adherent adherent);
    
    /**
     * Find all penalties expiring soon (within X days)
     */
    @Query("SELECT p FROM Penalite p WHERE p.dateFin BETWEEN CURRENT_DATE AND :endDate ORDER BY p.dateFin ASC")
    List<Penalite> findPenaltiesExpiringSoon(@Param("endDate") LocalDate endDate);
}
