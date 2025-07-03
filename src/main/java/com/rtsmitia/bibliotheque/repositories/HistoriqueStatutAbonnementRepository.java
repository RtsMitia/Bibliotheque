package com.rtsmitia.bibliotheque.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rtsmitia.bibliotheque.models.HistoriqueStatutAbonnement;
import com.rtsmitia.bibliotheque.models.HistoriqueStatutAbonnement.StatutAbonnement;

import java.util.List;

@Repository
public interface HistoriqueStatutAbonnementRepository extends JpaRepository<HistoriqueStatutAbonnement, Long> {
    
    List<HistoriqueStatutAbonnement> findByAdherentId(Long adherentId);
    
    List<HistoriqueStatutAbonnement> findByStatut(StatutAbonnement statut);
    
    @Query("SELECT h FROM HistoriqueStatutAbonnement h " +
           "WHERE h.adherent.id = :adherentId " +
           "ORDER BY h.dateChangement DESC, h.id DESC")
    List<HistoriqueStatutAbonnement> findMostRecentStatusByAdherentId(@Param("adherentId") Long adherentId);
}
