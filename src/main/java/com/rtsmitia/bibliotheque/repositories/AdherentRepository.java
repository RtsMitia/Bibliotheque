package com.rtsmitia.bibliotheque.repositories;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rtsmitia.bibliotheque.models.Adherent;

@Repository
public interface AdherentRepository extends JpaRepository<Adherent, Integer>{
    
    Optional<Adherent> findByEmail(String email);
    
    @Query("SELECT a FROM Adherent a " +
           "JOIN a.historiqueStatuts h " +
           "WHERE h.statut = com.rtsmitia.bibliotheque.models.HistoriqueStatutAbonnement.StatutAbonnement.demande " +
           "AND h.dateChangement = (" +
           "   SELECT MAX(h2.dateChangement) FROM a.historiqueStatuts h2" +
           ")")
    List<Adherent> findAdherentsWithStatusDemande();
    
    @Query("SELECT DISTINCT a FROM Adherent a " +
           "JOIN a.historiqueStatuts h " +
           "WHERE h.statut = :statut")
    List<Adherent> findAdherentsByStatut(@Param("statut") com.rtsmitia.bibliotheque.models.HistoriqueStatutAbonnement.StatutAbonnement statut);
}
